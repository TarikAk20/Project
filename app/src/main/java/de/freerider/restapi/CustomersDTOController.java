package de.freerider.restapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import de.freerider.datamodel.Customer;
import de.freerider.repository.CustomerRepository;
import de.freerider.restapi.dto.CustomerDTO;


@RestController
public class CustomersDTOController implements CustomersDTOAPI{
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	//
	private final HttpServletRequest request;
	
	
	public CustomersDTOController(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public ResponseEntity<List<CustomerDTO>> getCustomers() {
		System.err.println(request.getMethod() + " " + request.getRequestURI());
		List<CustomerDTO> list = new ArrayList<CustomerDTO>();
		for(Customer customer: customerRepository.findAll()) {
			CustomerDTO customerDTO = new CustomerDTO(customer);
			list.add(customerDTO);
		}
		return new ResponseEntity<List<CustomerDTO>>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<CustomerDTO> getCustomer(long id) {
		System.err.println(request.getMethod() + " " + request.getRequestURI());
		Optional<Customer> customerOpt = customerRepository.findById(id);
		if(customerOpt.isEmpty()) {
			return new ResponseEntity<CustomerDTO>(HttpStatus.NOT_FOUND);
		}
		Customer c = customerOpt.get();		
		CustomerDTO dto = new CustomerDTO(c);	
		return new ResponseEntity<CustomerDTO>(dto,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<CustomerDTO>> postCustomers(List<CustomerDTO> dtos) {
		System.err.println("POST /customers");
		if(dtos == null) {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
		for(CustomerDTO dto: dtos) {
			Optional<Customer> customerOpt = dto.create();
			if(customerOpt.isEmpty()) {
				return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			}
			Customer customer = customerOpt.get();
			if(customerRepository.existsById(customer.getId())) {
				return new ResponseEntity<>(null,HttpStatus.CONFLICT);
			}
			customerRepository.save(customer);	
		}
		return new ResponseEntity<>(dtos,HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<List<CustomerDTO>> putCustomers(List<CustomerDTO> dtos) {
		System.err.println(request.getMethod() + "" + request.getRequestURI());
		List<CustomerDTO> list = new ArrayList<>();
		for(CustomerDTO customerDTO : dtos) {
			Optional<Customer> customerOpt = customerDTO.create();
			if(customerOpt.isEmpty()) {
				System.out.println("Customer is Invalid");
				list.add(customerDTO);
			} else if (customerRepository.findById(customerOpt.get().getId()).isEmpty()) {
				System.out.println("Customer not found");
				list.add(customerDTO);
			}else {
				Customer customer = customerRepository.findById((customerOpt.get().getId())).get();
				customerRepository.deleteById(customerOpt.get().getId());
				customerRepository.save(customerOpt.get());
			}
		}
		if(list.size()>0) {
			return new ResponseEntity<>(list, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<List<CustomerDTO>>(dtos,HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<?> deleteCustomer(long id) {
		if(!customerRepository.existsById(id)) {
			return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
		}
		customerRepository.deleteById(id);
		System.err.println("DELETE /customer/" + id);
		return new ResponseEntity<>(null,HttpStatus.ACCEPTED);
	}
}
