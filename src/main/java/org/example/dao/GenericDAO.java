package org.example.dao;

import org.example.util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class GenericDAO<T, ID extends Serializable> {

    protected final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T find(ID id) {
        return executeRead(em -> em.find(entityClass, id));
    }

    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return executeRead(em -> em.createQuery(jpql, entityClass).getResultList());
    }

    public void create(T entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    public T update(T entity) {
        return executeInTransactionWithResult(em -> em.merge(entity));
    }

    public void delete(T entity) {
        executeInTransaction(em -> {
            T attached = em.contains(entity) ? entity : em.merge(entity);
            em.remove(attached);
        });
    }

    protected <R> R executeRead(Function<EntityManager, R> work) {
        EntityManager em = HibernateUtil.createEntityManager();
        try {
            return work.apply(em);
        } finally {
            em.close();
        }
    }

    protected void executeInTransaction(Consumer<EntityManager> work) {
        executeInTransactionWithResult(em -> {
            work.accept(em);
            return null;
        });
    }

    protected <R> R executeInTransactionWithResult(Function<EntityManager, R> work) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = work.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}