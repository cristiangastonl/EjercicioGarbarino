package com.garbarino.productos.service.impl;

import java.net.URI;

import javax.validation.ValidationException;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.garbarino.productos.Response.GenericResponse;
import com.garbarino.productos.entity.Brand;
import com.garbarino.productos.entity.Product;
import com.garbarino.productos.jpa.ProductRepository;
import com.garbarino.productos.service.GenericService;


/**
 * @author Gaston
 *
 */
@Service
public class ProductServiceImpl implements GenericService<Product, Integer> {
	
	private final static String VALIDACION_NOMBRE_NULL_VACIO = "Se debe ingresar un nombre";
	private final static String VALIDACION_DESCRIPCION_NULL_VACIO = "Se debe ingresar una descripcion no vacia";
	private final static String VALIDACION_PRECIO_POSITIVO = "Se debe ingresar un price positivo";
	private final static String VALIDACION_BRAND_VALIDO = "Se debe ingresar un brand válido. e.g: GARBARINO, COMPUMUNDO";
	
	private final ProductRepository productRepository;
	
	public ProductServiceImpl(final ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public Integer getId(Product entity) {
		return entity.getId();
	}

	@Override
	public CrudRepository<Product, Integer> getRepository() {
		return this.productRepository;
	}
	
	/**
	 * 	Metodo que devuelve los productos del repositorio bajo las siguientes condiciones:
	 * <br>
	 * <li> Si brand != null, entonces filtra por los productos de ese brand.
	 * <li> Si active != null y active == false, entonces filtra por los productos que no tienen stock.
	 * <li> Si active != null y active == true, entonces filtra por los productos que tiene stock.
	 * <li> Si brand == null y active == null, entonces devuelve todos los productos.
	 * <li> si brand == null y active == true, entocnes filtra solo por los productos con stock.
	 * <li> Si brand == null y active == false, entonces filtra por los productos sin stock
	 * <br>
	 * 
	 * @param brand
	 * @param active
	 * @return 
	 */
	public ResponseEntity<GenericResponse<Iterable<Product>>> findAllByCondition(Brand brand, Boolean active) {
		Iterable<Product> productos = null;
		if (brand == null && active == null) {
			// caso en el que no se especifica el filtro
			productos = this.findAll();
		} else if(brand != null && active ==null) {
			// caso en el que solo se especifica el brand
			productos = productRepository.findByBrand(brand);
		} else if(brand != null && active) {
			// caso en el que se especifica el brand y que tenga stock
			productos = productRepository.findByBrandAndStock(brand);
		} else if(brand != null && !active) {
			// caso en el que se especifica el brand y que no tenga stock
			productos = productRepository.findByBrandAndNoStock(brand);
		} else if(brand == null && !active) {
			// caso en el que no se especifica el brand y que tenga stock.
			productos = productRepository.findByNoStock();
		} else if(brand == null && active) {
			// caso en el que no se especifica el brand y que no tenga stock.
			productos = productRepository.findByPositiveStock();
		}
		
		return ResponseEntity.ok(new GenericResponse<>(productos));
	}

	/**
	 * 	Metodo que actuliza dado un id, y un stock, el producto que corresponde a ese id.
	 * 	<br>
	 * 	<li>Si el stock es mayor a 0, entonces lo actualiza sumando.<br>
	 * 	<li>Si el stock es menor a 0, entonces se actualiza restando.<br>
	 * 	<li>Se valida que no se actualize un stock con un numero negativo.
	 * 
	 * @param id
	 * @param stock
	 * @param result
	 * @return
	 */
	public ResponseEntity<StringBuilder> updateStock(Integer id, Integer stock, StringBuilder result) {
		ResponseEntity<StringBuilder> response;
		Product productoAActualizar = this.get(id);		
		Integer stockActual = productoAActualizar.getStock();
		Integer stockResultante = stockActual + stock;
		
		if (productoAActualizar == null || stockResultante < 0) {
			result.append("Error al modificar el Stock!");
			response = ResponseEntity.badRequest().body(result);
		} else {
			productoAActualizar.setStock(stock);
			this.update(productoAActualizar);
			result.append("Stock modificado correctamente!");
			response = ResponseEntity.ok(result);
		}
		return response;
	}
	
	/**
	 * 	Agrega un nuevo producto al repositorio de productos
	 * 	<br>
	 * 	<li> Se agrega con stock en 0.
	 * 
	 * @param product
	 * @return
	 */
	public ResponseEntity<GenericResponse<Product>> addNewProduct(Product product) throws ValidationException{
		
		this.validateInputsProduct(product);
		Product savedProduct = this.save(product);
		ResponseEntity<GenericResponse<Product>> response = ResponseEntity.created(URI.create("/" + product.getId())).body(new GenericResponse<>(savedProduct));
		return response;
	}

	/**
	 * 	Metodo que se encarga de validar los inputs de un producto.
	 * 
	 * @param product
	 */
	private void validateInputsProduct(Product product) throws ValidationException{
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
	private boolean esUnBrandValido(Brand brand) {
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
