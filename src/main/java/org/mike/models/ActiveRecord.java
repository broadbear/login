package org.mike.models;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.MultivaluedMap;

import net.sf.cglib.core.ReflectUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.bson.types.ObjectId;
import org.mike.config.Config;
import org.mike.config.Routes;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Entity
public abstract class ActiveRecord {
	final static Logger log = LoggerFactory.getLogger(ActiveRecord.class);
	final static String DB_NAME = Config.getSingleton().getDbName();
	static Morphia morphia;
	static Datastore ds;

	static {
		morphia = new Morphia();
		morphia.map(User.class); // TODO: bad to hard code here.
		morphia.map(Role.class);
		morphia.map(Permission.class);
		ds = morphia.createDatastore(getMongoClient(), DB_NAME);
		ds.ensureIndexes();
		ds.ensureCaps();
	}
	
	private String collectionName;
	
	@Id ObjectId _id;
	long createdAt;
	long updatedAt;
	Set<ConstraintViolation<? extends ActiveRecord>> errors = new HashSet<ConstraintViolation<? extends ActiveRecord>>();

	ActiveRecord() {
		collectionName = StringUtils.lowerCase(English.plural(getClass().getSimpleName()));
	}

	public static <E extends ActiveRecord> E createNew(Class<E> clazz) {
		@SuppressWarnings("unchecked")
		E ar = (E)ReflectUtils.newInstance(clazz);
		ar.beforeCreate();
		ar.createdAt = System.currentTimeMillis();
		ar.updatedAt = System.currentTimeMillis();
		return ar;
	}

	public static <E extends ActiveRecord> E createNew(Class<E> clazz, E entity) {
		entity.beforeCreate();
		entity.createdAt = System.currentTimeMillis();
		entity.updatedAt = System.currentTimeMillis();
		return entity;
	}

	public <T extends ActiveRecord> boolean save() {
		if (!validates()) {
			return false;
		}
		beforeSave();
		
		this.createdAt = System.currentTimeMillis();
		this.updatedAt = System.currentTimeMillis();
		
		ds.save(this);
		if (this._id != null) { // TODO: is this a valid check for success?
			return true;
		}
		return false;
	}
	
	public static <E extends ActiveRecord> List<E> all(Class<E> clazz) {
		List<E> entities = ds.find(clazz).asList();
		return entities;
	}

	public static <E extends ActiveRecord> E find(Class<E> clazz, String id) {
		E entity = (E) ds.get(clazz, new ObjectId(id));
		return entity;
	}

	public static <E extends ActiveRecord> E findBy(Class<E> clazz, String key, String value) {
		E entity = (E) ds.find(clazz, key, value).get();
		return entity;
	}

	/**
	 * Validates and updates supplied attributes on this instance.
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends ActiveRecord> boolean updateAttributes(MultivaluedMap<String, String> attributes) {
		if (!validates(attributes)) {
			return false;
		}
		// TODO: if all validate, set values on local instance and update attributes in persist.

		UpdateOperations<T> ops = (UpdateOperations<T>) ds.createUpdateOperations(getClass());
		ops.disableValidation(); // TOOD: not my favorite thing to do here, but getting exception for some reason
		for (Entry<String, List<String>> e: attributes.entrySet()) {
			ops.set(e.getKey(), e.getValue().get(0));
		}
		ops.set("updatedAt", System.currentTimeMillis());

		// TODO: this.id could be null if User instance is result of createNew(), seems sloppy
		if (this._id == null) {
			return false;
		}
		Query<T> updateQuery = (Query<T>) ds.createQuery(getClass()).field(Mapper.ID_KEY).equal(_id);
		ds.update(updateQuery, ops);
		return true;
	}
	
	public <T extends ActiveRecord> void updateAttribute(String attr, Object value) {
		UpdateOperations<T> ops = (UpdateOperations<T>) ds.createUpdateOperations(getClass()).set(attr, value);
		Query<T> updateQuery = (Query<T>) ds.createQuery(getClass()).field(Mapper.ID_KEY).equal(_id);
		ds.update(updateQuery, ops);
	}

	public <T extends ActiveRecord> void destroy() {
		ds.delete(getClass(), this._id);
	}

	static <T extends ActiveRecord> void destroyAll(Class<T> clazz) {
		ds.delete(ds.createQuery(clazz));
	}
	
	void beforeCreate() {
	}
	
	void beforeSave() {		
	}

	/**
	 * Validate this instance 'en masse.' Performs all 'default' validations
	 * on this instance. For use with saves.
	 * 
	 * @return
	 */
	boolean validates() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<? extends ConstraintViolation<?>> errors = validator.validate(this);
		addToErrors(errors);
		return this.errors.size() == 0;
	}
	
	/**
	 * For each attribute, if changed, perform validation. For use with
	 * updates.
	 * 
	 * @param attributes
	 * @return
	 */
	boolean validates(MultivaluedMap<String, String> attributes) {
		for (Entry<String, List<String>> e: attributes.entrySet()) {
			String propertyName = e.getKey();
			String newValue = attributes.getFirst(propertyName); // when attribute value is null, key exists, but value is empty list
			String currentValue = getProperty(this, propertyName);
			boolean changed = !StringUtils.equals(newValue, currentValue); // TODO: null is not equal to ""
			if (changed) {
				Set<? extends ConstraintViolation<?>> errors = validateValue(propertyName, newValue);
				addToErrors(errors);
			}
		}
		return this.errors.size() == 0;
	}
	
	Set<? extends ConstraintViolation<?>> validateValue(String propertyName, String value) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<? extends ConstraintViolation<?>> errors = 
				validator.validateValue(getClass(), propertyName, value);
		return errors;
	}

	void addToErrors(Set<? extends ConstraintViolation<?>> errors) {
		this.errors.addAll((Set<ConstraintViolation<? extends ActiveRecord>>)errors);
	}
	
	void update(DBObject query, DBObject dbObj) throws UnknownHostException {
		MongoClientURI dbUri = getDbUri();
		MongoClient client = new MongoClient(dbUri);
		DB db = client.getDB(dbUri.getDatabase());
		DBCollection coll = db.getCollection(collectionName);
		coll.update(query, dbObj);			
		client.close();
	}

	<T, E> T getProperty(E entity, String attribute) {
		try {
			T digest = (T)BeanUtils.getProperty(entity, attribute);
			return digest;
		} catch (Exception e) {
			log.error("Problem getting bean property.", e);
		}
		return null;
	}

	static MongoClientURI getDbUri() {
		MongoClientURI dbUri = new MongoClientURI(Config.getSingleton().getDbUri());
		return dbUri;
	}
	
	static MongoClient getMongoClient() {
		MongoClientURI dbUri = getDbUri();
		MongoClient client = null;
		try {
			client = new MongoClient(dbUri);
		} catch (UnknownHostException e) {
			log.error("Error instantiating mongo client.", e);
		}
		return client;
	}

	public String getId() {
		return _id.toString();
	}
	
	public void setId(String _id) {
		this._id = new ObjectId(_id);
	}

	public long getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}
	
	public long getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Set<? extends ConstraintViolation<?>> getErrors() {
		return errors;
	}
	
	public Routes getRoutes() {
		return new Routes();
	}
}
