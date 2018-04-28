package com.garbarino.productos.jpa;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.garbarino.productos.entity.Brand;
import com.garbarino.productos.entity.Product;


/**
 * @author Gaston
 *
 */
@Transactional
public interface ProductRepository extends JpaRepository<Product, Integer> {
	@Query("select p from Product p where p.brand = ?1 and p.stock > 0")
	List<Product> findByBrandAndStock(Brand brand);
	
	@Query("select p from Product p where p.brand = ?1 and p.stock = 0")
	List<Product> findByBrandAndNoStock(Brand brand);
	
	List<Product> findByBrand(Brand brand);
	
	@Query("select p from Product p where p.stock > 0")
	List<Product> findByPositiveStock();
	
	@Query("select p from Product p where p.stock = 0")
	List<Product> findByNoStock();
}
