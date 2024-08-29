package base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    private final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    public Optional<T> findById(ID id) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.id = :id";
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        query.setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        return query.getResultList();
    }

    public T save(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
        return entity;
    }

    public T update(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            T updatedEntity = entityManager.merge(entity);
            transaction.commit();
            return updatedEntity;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void delete(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    public long count() {
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }

    public List<T> findAllSorted(String sortField, boolean ascending) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e ORDER BY e." + sortField + (ascending ? " ASC" : " DESC");
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        return query.getResultList();
    }

    public List<T> findByField(String fieldName, Object value) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + fieldName + " = :value";
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        query.setParameter("value", value);
        return query.getResultList();
    }
}
