/**
 * 
 */
package com.garbarino.productos.validator;

import javax.validation.ValidationException;

import org.springframework.util.StringUtils;

import com.garbarino.productos.entity.Brand;
import com.garbarino.productos.entity.Product;

/**
 * 
 * @author Gaston
 *
 */
public class Validator {
	
	
	private final static String VALIDACION_NOMBRE_NULL_VACIO = "Se debe ingresar un nombre";
	private final static String VALIDACION_DESCRIPCION_NULL_VACIO = "Se debe ingresar una descripcion no vacia";
	private final static String VALIDACION_PRECIO_POSITIVO = "Se debe ingresar un price positivo";
	private final static String VALIDACION_BRAND_VALIDO = "Se debe ingresar un brand vÃ¡lido. e.g: GARBARINO, COMPUMUNDO";
	
	/**
	 * 	Metodo que se encarga de validar los inputs de un producto.
	 * 
	 * @param product
	 */
	public static void validateInputsProduct(Product product) throws ValidationException{
		boolean valido = true;
		String msg = "validaciones";
		if (!StringUtils.hasText(product.getName())){
			// valida que no sea null el nombre y que tenga al menos un caracter que no sea un espacio.
			valido = false;
			msg = String.format("%s, %s", msg, VALIDACION_NOMBRE_NULL_VACIO);					
		}
		if (!StringUtils.hasText(product.getDescription())) {
			// valida que no sea null la descripcion y que tenga al menos un caracter que no sea un espacio.
			valido = false;
			msg = String.format("%s, %s", msg, VALIDACION_DESCRIPCION_NULL_VACIO);	
		}
		if (product.getPrice() == null || product.getPrice() < 0 ) {
			// valida que el precio no sea nulo y que sea mayor a 0.
			valido = false;
			msg = String.format("%s, %s", msg, VALIDACION_PRECIO_POSITIVO);
		}
		if(product.getStock() == null || product.getStock() < 0) {
			// valida que el stock sea un numero positivo y que no sea null.
			valido = false;
			msg = String.format("%s, %s", msg, VALIDACION_PRECIO_POSITIVO);
		}
		if(product.getBrand() == null || !esUnBrandValido(product.getBrand())) {
			// valida que el brand sea uno valido y distinto de null.
			valido = false;
			msg = String.format("%s, %s", msg, VALIDACION_BRAND_VALIDO);
		}
		
		if (!valido) {
			throw new ValidationException(msg);
		}
		
	}

	/**
	 * 	Itera entre todos los posibles Brands y decide si el brand pasado por parametro es valido.
	 * 
	 * @param brand
	 * @return
	 */
	public static boolean esUnBrandValido(Brand brand) {
		Boolean exist = false;
		for (Brand nombre : Brand.values()) {
			if (exist = nombre.toString().equals(brand.toString())) {
				// Encontre uno, entonces es un brand valido.
				break;
			}
		}
		
		return exist;
	}
}
