package com.matchalab.trip_todo_api.generator;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class IdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(
            SharedSessionContractImplementor session,
            Object object) throws HibernateException {
        try {
            UUID id = (UUID) object.getClass().getMethod("getId").invoke(object);

            if (id != null) {
                return id;
            }
        } catch (Exception e) {
            throw new HibernateException(
                    "Could not access ID field or method on entity: " + object.getClass().getName(), e);
        }

        return UUID.randomUUID();
    }
}