package com.duggan.workflow.server.helper.auth;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.directory.server.core.authn.LdapPrincipal;
import org.apache.directory.shared.ldap.constants.AuthenticationLevel;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.server.ApacheDSContainer;

import sun.misc.BASE64Encoder;

import com.duggan.workflow.server.helper.ldap.LdapQueryHelper;
import com.duggan.workflow.shared.exceptions.InitializationFailureError;
import com.duggan.workflow.shared.model.HTUser;
import com.duggan.workflow.shared.model.UserGroup;

/**
 * This is a helper class for the authentication process
 * 
 * @author duggan
 * 
 */
public class LoginHelper implements Closeable {

	private static LoginHelper helper;
	private ApacheDSContainer container;
	private LdapQueryHelper ldapQuery;
	LdapTemplate ldapTemplate;
	private String usersBaseDN = "ou=users";

	private LoginHelper() {
		try {
			container = new ApacheDSContainer("o=mojo",
					"classpath:identity-repository.ldif");
			container.setPort(9898);
			container.afterPropertiesSet();

			LdapContextSource cs = new LdapContextSource();
			cs.setUrl("ldap://localhost:9898/");
			cs.setBase("o=mojo");
			cs.setUserDn("uid=admin,ou=system");
			cs.setPassword("secret");
			cs.afterPropertiesSet();
			ldapTemplate = new LdapTemplate(cs);
			// ldapTemplate.setIgnorePartialResultException(true);

			ldapTemplate.afterPropertiesSet();
			ldapQuery = new LdapQueryHelper(ldapTemplate);

		} catch (Exception e) {
			throw new InitializationFailureError("Could not initialize LDAP container : "+e.getMessage(), e);
		}
	}

	/**
	 * User AttributesMapper
	 */
	AttributesMapper userAttributesMapper = new AttributesMapper() {

		@Override
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException {

			HTUser user = new HTUser();
			Object name = attributes.get("cn") == null ? null : attributes.get(
					"cn").get();
			Object id = attributes.get("uid") == null ? null : attributes.get(
					"uid").get();
			Object email = attributes.get("mail") == null ? null : attributes
					.get("mail").get();
			Object surname = attributes.get("sn") == null ? null : attributes
					.get("sn").get();

			user.setName(name == null ? null : name.toString());
			user.setId(id == null ? null : id.toString());
			user.setEmail(email == null ? null : email.toString());
			user.setSurname(surname == null ? null : surname.toString());

			return user;
		}
	};

	/**
	 * Group Attributes Mapper
	 */
	AttributesMapper groupAttributesMapper = new AttributesMapper() {

		@Override
		public Object mapFromAttributes(Attributes attributes)
				throws NamingException {
			UserGroup group = new UserGroup();

			Object name = attributes.get("cn").get();
			group.setName(name.toString());

			return group;
		}
	};

	/**
	 * Using {@link LdapTemplate} for authentication.
	 * 
	 * Given an entry dn: uid=mary,ou=users,o=mojo Through try and error; the
	 * authentication of a user using LdapTemplate.authenticate takes the
	 * following inputs
	 * 
	 * -baseDn This becomes ou=users (instead of o=mojo - probably because
	 * o=mojo was set as the base of {@link LdapContextSource} above
	 * {@link #LoginHelper()} ) TODO: Go through Spring LDAP documentation to
	 * figure this out
	 * 
	 * -Filter string {@link Filter} Based on the above example new
	 * EqualsFilter("uid", "mary") -Password This is the password of the user
	 * (marys password in the example above)
	 * 
	 * @param username
	 * @param password
	 * @return true if a user with the given password & username is found
	 */
	public boolean login(String username, String password) {

		boolean isvalid = false;
		AndFilter filter = new AndFilter()
				.and(new EqualsFilter("uid", username));

		try {
			isvalid = ldapTemplate.authenticate("ou=users", filter.toString(),
					password);
		} catch (AuthenticationException ex) {
			String message = ex.getMessage();
			//log?
		}

		return isvalid;
	}

	/**
	 * Thread safe Singleton
	 * 
	 * @return LoginHelper
	 */
	public static LoginHelper get() {
		if (helper == null) {
			synchronized (LoginHelper.class) {
				if (helper == null) {
					helper = new LoginHelper();
				}
			}

		}

		return helper;
	}

	/**
	 * Retrieve All Users
	 * 
	 * @return
	 */
	public List<HTUser> retrieveUsers() {
		AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass",
				"person"));
		List lst = ldapTemplate.search("", filter.toString(),
				userAttributesMapper);

		return lst;
	}
	
	public HTUser getUser(String userId){
		
		AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass",
				"person"))
				.and(new EqualsFilter("uid", userId));
		List lst = ldapTemplate.search("", filter.toString(),
				userAttributesMapper);
		
		return (HTUser)lst.get(0);
	}

	/**
	 * 
	 * @return All groups
	 */
	public List<UserGroup> retrieveGroups() {
		AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass",
				"group"));
		List groups = ldapTemplate.search("", filter.toString(),
				groupAttributesMapper);

		return groups;
	}

	/**
	 * Create/Update New User
	 */
	public HTUser createUser(HTUser user) {
		
		DirContextOperations ctx = new DirContextAdapter(new DistinguishedName(
				"uid=" + user.getId() + "," + usersBaseDN));
		ctx.addAttributeValue("objectClass", "organizationalPerson");
		ctx.addAttributeValue("objectClass", "person");
		ctx.addAttributeValue("objectClass", "inetOrgPerson");
		ctx.addAttributeValue("objectClass", "top");
		ctx.addAttributeValue("cn", user.getName());
		ctx.addAttributeValue("sn", user.getSurname());
		ctx.addAttributeValue("mail", user.getEmail());
		
		ctx.addAttributeValue("userPassword", "{SHA}" + this.encrypt(user.getPassword()));

		ldapTemplate.bind(ctx);

		return user;
	}

	public boolean deleteUser(HTUser user) {
		boolean success = false;

		ldapTemplate.unbind("uid=" + user.getId() + ",ou=users,o=mojo");

		return success;
	}

	/**
	 * Note The original ldif maps the users using the common name : e.g
	 * uniqueMember: cn=salaboy,ou=users,o=mojo; therefore any attempts to
	 * retrieve groups for user using the uid i.e uniqueMember:
	 * uid=salaboy,ou=users,o=mojo; fails!!! - see EqualsFilter below
	 * 
	 * @param userId
	 * @return
	 */
	public List<UserGroup> getGroupsForUser(String userId) {
		// long start=System.currentTimeMillis();

		List groups = null;

		AndFilter filter = new AndFilter().and(
				new EqualsFilter("objectClass", "group")).and(
				new EqualsFilter("uniqueMember", "cn=" + userId
						+ ",ou=users,o=mojo"));

		groups = ldapTemplate.search("", filter.toString(),
				groupAttributesMapper);

		// long end= System.currentTimeMillis();
		// long millis = end-start;
		// System.err.println("Search in : "+millis+"ms");
		return groups;
	}

	@Override
	public void close() throws IOException {
		container.stop();
	}

	public static LoginHelper getHelper() {
		return helper;
	}

	public ApacheDSContainer getContainer() {
		return container;
	}

	public LdapQueryHelper getLdapQuery() {
		return ldapQuery;
	}

	private String encrypt(final String plaintext) {
		MessageDigest md = null;
		
		try {
			
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		try {
			md.update(plaintext.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		byte raw[] = md.digest();
		
		String hash = (new BASE64Encoder()).encode(raw);
		
		return hash;
	}

}
