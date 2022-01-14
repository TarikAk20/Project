package de.freerider.restapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.freerider.datamodel.Customer;
import de.freerider.repository.CustomerRepository;

import com.fasterxml.jackson.core.type.TypeReference;


//@RestController
public class CustomersController implements CustomersAPI {
	//
	@Autowired
    private ApplicationContext context;
	//
	private final ObjectMapper objectMapper;
	//
	private final HttpServletRequest request;
	
	@Autowired
	private CustomerRepository customerRepository;


	/**
	 * Constructor.
	 * 
	 * @param objectMapper entry point to JSON tree for the Jackson library
	 * @param request HTTP request object
	 */
	public CustomersController( ObjectMapper objectMapper, HttpServletRequest request ) {
		this.objectMapper = objectMapper;
		this.request = request;
	}
	/**
	 * GET /server/stop
	 * 
	 * Stop sever and shut down application.
	 * @return
	 */
	/*@Override
	public ResponseEntity<Void> stop() {
		//
 		try {
			System.err.print( request.getRequestURI() + " " + request.getMethod() + "\nshutting down server..." );
			//
			ApplicationContext context = this.context;
			((ConfigurableApplicationContext) context).close();
			//
			System.err.println( "  done." );
			//
            return new ResponseEntity<Void>( HttpStatus.OK );

        } catch( Exception e ) {
            return new ResponseEntity<Void>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
	}*/

	private ArrayNode peopleAsJSON(Optional<Customer> optional) {
		//
		ArrayNode arrayNode = objectMapper.createArrayNode();
		optional.ifPresent(c -> {
			StringBuffer sb = new StringBuffer();
			c.getContacts().forEach( contact -> sb.append( sb.length()==0? "" : "; " ).append( contact ) );
			arrayNode.add(objectMapper.createObjectNode()
				.put( "id", c.getId())
				.put( "name", c.getLastName())
				.put( "first", c.getFirstName())
				.put( "contacts", sb.toString()));
		});				
		return arrayNode;
	}
	
	private ArrayNode peopleAsJSON() {
		ArrayNode arrayNode = objectMapper.createArrayNode();
		
		customerRepository.findAll().forEach( c -> {
			StringBuffer sb = new StringBuffer();
			c.getContacts().forEach( contact -> sb.append( sb.length()==0? "" : "; " ).append( contact ) );
			arrayNode.add(
				objectMapper.createObjectNode()
					.put( "id", c.getId())
					.put( "name", c.getLastName())
					.put( "first", c.getFirstName())
					.put( "contacts", sb.toString() )
			);
		});
		return arrayNode;
	}

	@Override
	public ResponseEntity<List<?>> getCustomers() {
		ResponseEntity<List<?>> re = null;
		System.err.println( request.getMethod() + " " + request.getRequestURI());   
		try {
			ArrayNode arrayNode = peopleAsJSON();
			
			ObjectReader reader = objectMapper.readerFor( new TypeReference<List<ObjectNode>>() { } );
			List<String> list = reader.readValue( arrayNode );
			
			re = new ResponseEntity<List<?>>( list, HttpStatus.OK );
		} catch( IOException e ) {
			re = new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}


	@Override
	public ResponseEntity<?> getCustomer(long id) {
		//
		ResponseEntity<String> re = null;
		System.err.println(request.getMethod() + " " + request.getRequestURI());
		try {
			if (customerRepository.existsById(id)){
				ArrayNode arrayNode = peopleAsJSON(customerRepository.findById(id));
				
				String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
				
				re = new ResponseEntity<String>(pretty, HttpStatus.OK);				
			} else {
				re = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			} catch (IOException e) {
				re = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		return re;
	}
	@Override
	public ResponseEntity<List<?>> postCustomers(Map<String, Object>[] jsonMap) {
		System.err.println("POST /customers");
		if(jsonMap == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		Set<String> key = null;
		Collection<Object> value = null;
		for(int i = 0; i < jsonMap.length; i++) {
			Map<String, Object> map = jsonMap[i];
			
		}
		return null;
	}
	@Override
	public ResponseEntity<List<?>> putCustomers(Map<String, Object>[] jsonMap) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ResponseEntity<?> deleteCustomer(long id) {
		System.err.println("DELETE /customers/" + id);
		if(customerRepository.existsById(id)) {
			customerRepository.deleteById(id);
			return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
}
