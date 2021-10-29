package org.avni.framework.hibernate;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.joda.time.DateTime;
import org.avni.domain.Audit;
import org.avni.domain.OrganisationAwareEntity;
import org.avni.domain.User;
import org.avni.domain.Organisation;
import org.avni.domain.UserContext;
import org.avni.framework.security.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class UpdateOrganisationHibernateInterceptor extends EmptyInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(UpdateOrganisationHibernateInterceptor.class.getName());

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return updateOrganisationId(entity, state, propertyNames);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        boolean somethingChanged = false;
        int indexOf = getIndexOf(propertyNames, "audit");
        if (indexOf != -1 && currentState[indexOf] != null) {
            Audit audit = (Audit) currentState[indexOf];
            UserContext userContext = UserContextHolder.getUserContext();
            User user = userContext.getUser();
            logger.info(String.format("Username is %s", user.getUsername()));
            logger.info(String.format("Updating organisation interceptor for id %s", audit.getId()));
            if (audit.getCreatedBy() == null) {
                audit.setCreatedBy(user);
            }
            audit.setLastModifiedBy(user);
            audit.setLastModifiedDateTime(new DateTime());
            somethingChanged = true;
        }
        return updateOrganisationId(entity, currentState, propertyNames) || somethingChanged;
    }

    private int getIndexOf(String[] propertyNames, String propertyName) {
        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyNames[i].equals(propertyName)) return i;
        }
        return -1;
    }

    private boolean updateOrganisationId(Object entity, Object[] currentState, String[] propertyNames) {
        if (entity instanceof OrganisationAwareEntity) {
            int organisationIdIndex = getIndexOf(propertyNames, "organisationId");
            Organisation organisation = UserContextHolder.getUserContext().getOrganisation();
            if (organisation == null) {
                return false;
            }
            if (currentState[organisationIdIndex] == null) {
                currentState[organisationIdIndex] = organisation.getId();
                return true;
            }
        }
        return false;
    }
}