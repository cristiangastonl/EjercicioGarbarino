package com.garbarino.productos.vo;


/**
 * 	Clase generica que devuelven todos los servicios REST.
 *
 * @author Gaston
 *
 * @param <T>
 */
public class GenericResponse<T> {
	private T results;
	
	public GenericResponse(T results) {
		this.results = results;
	}
	
	public T getResults() {
		return this.results;
	}
}
