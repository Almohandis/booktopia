package repository;

import base.BaseRepository;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static utils.RequestParameterUtil.*;

public class ProductRepository extends BaseRepository<Product, Long> {
    public ProductRepository() {
        super(Product.class);
    }

    public List<Product> search(Map<String, String> queryParameters, int pageNumber, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> productRoot = query.from(Product.class);
        Predicate predicate = cb.conjunction();
        if (queryParameters.getOrDefault(MINIMUM_PRICE, null) != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(productRoot.get(PRICE), new BigDecimal(queryParameters.get(MINIMUM_PRICE))));
        }
        if (queryParameters.getOrDefault(MAXIMUM_PRICE, null) != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(productRoot.get(PRICE), BigDecimal.valueOf(Integer.parseInt(queryParameters.get(MAXIMUM_PRICE)))));
        }
        if (queryParameters.getOrDefault(CATEGORY, null) != null) {
            predicate = cb.and(predicate, cb.equal(productRoot.get(CATEGORY).get("id"), Long.parseLong(queryParameters.get(CATEGORY))));
        }
        if (queryParameters.getOrDefault(NAME, null) != null) {
            predicate = cb.and(predicate, cb.like(productRoot.get(NAME), "%" + queryParameters.get(NAME) + "%"));
        }
        predicate = cb.and(predicate, cb.equal(productRoot.get(IS_DELETED), false));
        query.select(productRoot).where(predicate);
        return entityManager.createQuery(query).setFirstResult(pageNumber * pageSize).setMaxResults(pageSize).getResultList();
//        return entityManager.createQuery(query).getResultList();
    }

    public List<Product> findAllAvailable() {
        String jpql = "SELECT p FROM Product p WHERE p.isDeleted = false AND p.quantity > 0";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        return query.getResultList();
    }

    public Product findAvailableProductById(Long id) {
        String jpql = "SELECT p FROM Product p WHERE p.id = :id AND p.isDeleted = false AND p.quantity > 0";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    public boolean existsByIsbn(String isbn) {
        String jpql = "SELECT COUNT(p) FROM Product p WHERE p.isbn = :isbn";
        Long count = entityManager.createQuery(jpql, Long.class).setParameter("isbn", isbn).getSingleResult();
        return count > 0;
    }
}
