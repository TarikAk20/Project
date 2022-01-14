package de.freerider.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

import de.freerider.datamodel.Customer;

@Component
public class CustomerRepository implements CrudRepository<Customer, Long>{
	private HashMap<Long, Customer> repository = new HashMap<Long, Customer>();
	
    @SuppressWarnings("unchecked")
	@Override
    public <S extends Customer> S save(S entity) {
        if(entity != null) {
        	Long id = entity.getId();
        	if(id < 0L) {
        		id = Long.valueOf((long)(Math.random() * 1000.0));
        		while(repository.containsKey(id)) {
        			id = Long.valueOf((long)(Math.random() * 1000.0));
        		}
        		entity.setId((long)id);
        	}
        	if(!repository.containsKey(id)) {
        		repository.put(id, entity);
        		
        	} else {
        		Customer old = repository.remove(id);
        		repository.put(id, entity);
        		return (S) old;
        	}
        	
        } else {
        	throw new IllegalArgumentException();
        }
        return entity;
    }

    @Override
    public <S extends Customer> Iterable<S> saveAll(Iterable<S> entities) {
    	ArrayList<S> entitiesList = new ArrayList<>();
    	if(entities != null) {
        	for (S toSaveEntities : entities) {
        		entitiesList.add(toSaveEntities);
        		save(toSaveEntities);
        	}
        } else {
        	throw new IllegalArgumentException();
        }
        return entitiesList;
    }

    @Override
    public boolean existsById(Long id) {
    	if(id != null) {
    		return repository.containsKey(id); 
    	} else {
    		throw new IllegalArgumentException();
    	}
	}

    @Override
    public Optional<Customer> findById(Long id) {
        if(id != null) {
        	return Optional.ofNullable(repository.get(id));
        } else {
        	throw new IllegalArgumentException(); 
        }
    }

    @Override
    public Iterable<Customer> findAll() {
        return repository.values();
    }    	    

    @Override
    public Iterable<Customer> findAllById(Iterable<Long> ids) {
    	ArrayList<Customer> values = new ArrayList<>();
    	if(ids != null) {		
    		for(Long iD : ids) {			
    			Optional<Customer> element = findById(iD);
    			if(element.isPresent()) {
    				values.add(element.get());				
    			}			
    		}
    	} else {
    		throw new IllegalArgumentException();
    	}
    	return values;
    }

    @Override
    public long count() {
    	return repository.size();
    }

    @Override
    public void deleteById(Long id) {
    	if(id != null) {
    		repository.remove(id);
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    @SuppressWarnings("unlikely-arg-type")
	@Override
    public void delete(Customer entity) {
        if(entity != null) {
        	repository.remove(entity);
        }else {
        	throw new IllegalArgumentException();
        } 	
    }

    @SuppressWarnings("unlikely-arg-type")
	@Override
    public void deleteAllById(Iterable<? extends Long> ids) {
    	if(ids != null) {		
    		for(Long key : ids) {			
    			Optional<Customer> element = findById(key);
    			if(element.isPresent()) {
    				repository.remove(element);			
    			}			
    		}
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    @SuppressWarnings("unlikely-arg-type")
	@Override
    public void deleteAll(Iterable<? extends Customer> entities) {
    	if(entities != null) {	
    		for(Customer c : entities) {			
    			repository.remove(c);	
    		}
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    @Override
    public void deleteAll() {
    	repository.clear();
    }
}