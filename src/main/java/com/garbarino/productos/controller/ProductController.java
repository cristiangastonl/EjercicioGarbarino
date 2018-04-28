package com.garbarino.productos.controller;

import javax.validation.ValidationException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.garbarino.productos.Response.GenericResponse;
import com.garbarino.productos.entity.Brand;
import com.garbarino.productos.entity.Product;
import com.garbarino.productos.service.impl.ProductServiceImpl;


/**
 * @author Gaston
 *
 */
@RestController
@RequestMapping("/products")
public class ProductController {
	
	private final ProductServiceImpl productServiceImpl;

	public ProductController(final ProductServiceImpl productService) {
		this.productServiceImpl = productService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<Iterable<Product>>> findAll(@RequestParam(name="brand", required=false) Brand brand, @RequestParam(name="active", required=false) Boolean active) {
		ResponseEntity<GenericResponse<Iterable<Product>>> response = productServiceImpl.findAllByCondition(brand, active);
		return response;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GenericResponse<Product>> create(@RequestBody Product product) throws ValidationException{	
		ResponseEntity<GenericResponse<Product>> response;
		try {
			response = productServiceImpl.addNewProduct(product);
		} catch (ValidationException e) {
			throw e;
		}	
		return response;
	}

	@PutMapping(value = "/{id}/stock")
	public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestParam(value="q") Integer stock ) {
		ResponseEntity<StringBuilder> response = productServiceImpl.updateStock(id, stock); 		
		return response;
	}

}