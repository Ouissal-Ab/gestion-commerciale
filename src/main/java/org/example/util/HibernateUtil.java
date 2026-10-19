package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;


public final class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static EntityManager createEntityManager() {
        return sessionFactory.createEntityManager();
    }

    public static void shutdown() {
        if (!sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}