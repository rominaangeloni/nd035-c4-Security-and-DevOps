package com.wallapop.iam.keycloak.extensions.monolithusers;

import com.wallapop.iam.keycloak.extensions.monolithusers.password.ClearTextPassword;
import com.wallapop.iam.keycloak.extensions.monolithusers.password.HashedPassword;
import com.wallapop.iam.keycloak.extensions.monolithusers.password.PasswordValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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

public class MonolithUserProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    // Temporary list of users that can use the wallapop-internal realm
    private static final List<String> ALLOWED_INTERNAL_USERS = Arrays.asList(
            // TnS Team
            "albert.sabate@wallapop.com",
            "alejandro.ibanez@wallapop.com",
            "gerard.casamitjana@wallapop.com",
            "izaskun.perez@wallapop.com",
            "jazz.villanego@wallapop.com",
            "jordi.fernandez@wallapop.com",
            "laialopezz99@gmail.com",
            "l3slie@hotmail.fr",
            "nerea.cots@wallapop.com",
            "oriolgr@protonmail.com",
            "xavi.figueras@wallapop.com",

            // Platform Backbone Team
            "carlos.martinez@wallapop.com",
            "david.belenguer@wallapop.com",
            "david.castro@wallapop.com",
            "eduard.lopez@wallapop.com",
            "gerard.llorente@wallapop.com",
            "javier.carbajo@wallapop.com",
            "josep.anguera@wallapop.com",
            "nil.font@wallapop.com",
            "oscar.ruiz@wallapop.com",
            "raquel.guimaraes@wallapop.com",
            "romina.angeloni@wallapop.com"
    );

    public static final String PASSWORD_CACHE_KEY = MonolithUserAdapter.class.getName() + ".password";
    private static final Logger logger = LoggerFactory.getLogger(MonolithUserProvider.class);

    protected EntityManager entityManagerMonolith;
    protected EntityManager entityManagerAuth;
    protected ComponentModel model;
    protected KeycloakSession session;

    MonolithUserProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
        entityManagerMonolith = this.session.getProvider(JpaConnectionProvider.class, "user-store").getEntityManager();
        entityManagerAuth = this.session.getProvider(JpaConnectionProvider.class, "user-store-auth").getEntityManager();
    }

    @Override
    public void preRemove(RealmModel realm) {
        // Not aplicable
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        // Not aplicable
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        // Not aplicable
    }

    @Override
    public void close() {
        // Not aplicable
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        logger.info("getUserById: {}", id);
        String persistenceId = StorageId.externalId(id);

        MonolithUser entity;
        if (realm.getName().equals("wallapop-connect")) {
            entity = entityManagerMonolith.find(MonolithUser.class, persistenceId);
        } else {
            entity = Optional.ofNullable(entityManagerAuth.find(AuthUser.class, persistenceId))
                    .map(this::mapAuthUserToMonolithUser)
                    .filter(user -> ALLOWED_INTERNAL_USERS.contains(user.getEmail()))
                    .orElse(null);
        }

        if (entity == null) {
            logger.info("Could not find user by id: {}", id);
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
        MonolithUser user;

        if (realm.getName().equals("wallapop-connect")) {
            user = getMonolithUserForConnect(email);
        } else {
            user = getMonolithUserForInternal(email);
        }

        if (user == null) return null;

        return new MonolithUserAdapter(session, realm, model, user);
    }

    private MonolithUser getMonolithUserForConnect(String email) {
        TypedQuery<MonolithUser> query = entityManagerMonolith.createNamedQuery("getProUserByEmail",
                MonolithUser.class);
        query.setParameter("email", email);
        List<MonolithUser> result = query.getResultList();
        if (result.isEmpty()) return null;
        logger.info("User found in Monolith {}", email);

        Optional<AuthUser> resultAuth = getAuthUser(email);
        AtomicReference<MonolithUser> user = new AtomicReference<>();
        resultAuth.ifPresent(authUser -> {
            user.set(result.get(0));
            user.get().setAuthPassword(authUser.getPassword());
        });

        return user.get();
    }

    private MonolithUser getMonolithUserForInternal(String email) {
        return getAuthUser(email)
                .map(this::mapAuthUserToMonolithUser)
                .filter(user -> ALLOWED_INTERNAL_USERS.contains(user.getEmail()))
                .orElse(null);
    }

    private MonolithUser mapAuthUserToMonolithUser(AuthUser authUser) {
        MonolithUser monolithUser = new MonolithUser();
        monolithUser.setUserId(authUser.getId());
        monolithUser.setFirstName(authUser.getEmail());
        monolithUser.setEmail(authUser.getEmail());
        monolithUser.setPassword(authUser.getPassword());
        monolithUser.setAuthPassword(authUser.getPassword());
        monolithUser.setUserPerks(new HashSet<>());
        return monolithUser;
    }

    private Optional<AuthUser> getAuthUser(String email) {
        TypedQuery<AuthUser> queryAuth = entityManagerAuth.createNamedQuery("getUserByEmail", AuthUser.class);
        queryAuth.setParameter("email", email);

        Optional<AuthUser> result = queryAuth.getResultList().stream().findFirst();

        result.ifPresentOrElse(
                it -> logger.info("User found in Auth {}", email),
                () -> logger.info("User not found in Auth {}", email)
        );

        return result;
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
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel cred)) return false;

        String password = getPassword(user);

        boolean isValid = PasswordValidator.sameHash(new ClearTextPassword(cred.getValue()), new HashedPassword(password), logger);
        if (isValid) {
            logger.info("User validation success with monolith database {}", user.getEmail());
            return true;
        }
        logger.info("User validation was unsuccessful for user {}", user.getEmail());
        return false;
    }

    private String getPassword(UserModel user) {
        String password = null;
        if (user instanceof CachedUserModel cachedUserModel) {
            password = (String) cachedUserModel.getCachedWith().get(PASSWORD_CACHE_KEY);
        } else if (user instanceof MonolithUserAdapter monolithUserAdapter) {
            password = monolithUserAdapter.getPassword();
        }
        return password;
    }
}
