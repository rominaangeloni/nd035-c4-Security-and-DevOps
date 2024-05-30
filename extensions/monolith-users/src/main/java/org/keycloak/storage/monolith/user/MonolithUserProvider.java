package org.keycloak.storage.monolith.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MonolithUserProvider implements UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator
{
    private static final Logger logger = LoggerFactory.getLogger(MonolithUserProvider.class);
    public static final String PASSWORD_CACHE_KEY = MonolithUserAdapter.class.getName() + ".password";

    protected EntityManager em;

    protected ComponentModel model;
    protected KeycloakSession session;

    MonolithUserProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        em = this.session.getProvider(JpaConnectionProvider.class, "user-store").getEntityManager();
    }

    @Override
    public void preRemove(RealmModel realm) {

    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {

    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {

    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        logger.info("getUserById: " + id);
        String       persistenceId = StorageId.externalId(id);
        MonolithUser entity        = em.find(MonolithUser.class, persistenceId);
        if (entity == null) {
            logger.info("could not find user by id: " + id);
            return null;
        }
        return new MonolithUserAdapter(session, realm, model, entity);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        //We don't need to find users by username. If this method returns null KC will look for email
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        TypedQuery<MonolithUser> query = em.createNamedQuery("getProUserByEmail", MonolithUser.class);
        query.setParameter("email", email);
        List<MonolithUser> result = query.getResultList();
        if (result.isEmpty()) return null;
        logger.info("User found");
        return new MonolithUserAdapter(session, realm, model, result.get(0));
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType) && getPassword(user) != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        logger.info("Validating user");
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
        UserCredentialModel cred = (UserCredentialModel)input;
        String password = getPassword(user);
        return password != null && password.equals(cred.getValue());
    }

    private String getPassword(UserModel user) {
        String password = null;
        if (user instanceof CachedUserModel) {
            password = (String)((CachedUserModel)user).getCachedWith().get(PASSWORD_CACHE_KEY);
        } else if (user instanceof MonolithUserAdapter) {
            password = ((MonolithUserAdapter)user).getPassword();
        }
        return password;
    }
}
