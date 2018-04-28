package com.garbarino.productos.service;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.garbarino.productos.service.exception.GenericServiceException;


/**
 * 	Excepcion de la capa service.
 * 
 * @author Gaston
 *
 * @param <T>
 * @param <ID>
 */
public interface GenericService<T, ID extends Serializable> {
	
	default Iterable<T> findAll() {
		return getRepository().findAll();
	}
	
	default T get(ID id) {
		return getRepository().findOne(id);
	}
	
	default T save(T entity) {
		return getRepository().save(entity);
	}
		
	default void update(T entity) {
		if (getRepository().exists(getId(entity))) {
			getRepository().save(entity);
		}
		else {
			throw new GenericServiceException("No se puede modificar el producto, ya que no existe en la base de datos " + entity);
		}
	}
	
	ID getId(T entity);
	
	CrudRepository<T, ID> getRepository();
}
