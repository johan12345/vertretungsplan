package com.johan.vertretungsplan_2;

import com.johan.vertretungsplan_2.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "vertretungsplanserializedendpoint", namespace = @ApiNamespace(ownerDomain = "johan.com", ownerName = "johan.com", packagePath = "vertretungsplan_2"))
public class VertretungsplanSerializedEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listVertretungsplanSerialized")
	public CollectionResponse<VertretungsplanSerialized> listVertretungsplanSerialized(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<VertretungsplanSerialized> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr
					.createQuery("select from VertretungsplanSerialized as VertretungsplanSerialized");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<VertretungsplanSerialized>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (VertretungsplanSerialized obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<VertretungsplanSerialized> builder()
				.setItems(execute).setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getVertretungsplanSerialized")
	public VertretungsplanSerialized getVertretungsplanSerialized(
			@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		VertretungsplanSerialized vertretungsplanserialized = null;
		try {
			vertretungsplanserialized = mgr.find(
					VertretungsplanSerialized.class, id);
		} finally {
			mgr.close();
		}
		return vertretungsplanserialized;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param vertretungsplanserialized the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertVertretungsplanSerialized")
	public VertretungsplanSerialized insertVertretungsplanSerialized(
			VertretungsplanSerialized vertretungsplanserialized) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsVertretungsplanSerialized(vertretungsplanserialized)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(vertretungsplanserialized);
		} finally {
			mgr.close();
		}
		return vertretungsplanserialized;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param vertretungsplanserialized the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateVertretungsplanSerialized")
	public VertretungsplanSerialized updateVertretungsplanSerialized(
			VertretungsplanSerialized vertretungsplanserialized) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsVertretungsplanSerialized(vertretungsplanserialized)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(vertretungsplanserialized);
		} finally {
			mgr.close();
		}
		return vertretungsplanserialized;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeVertretungsplanSerialized")
	public void removeVertretungsplanSerialized(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		try {
			VertretungsplanSerialized vertretungsplanserialized = mgr.find(
					VertretungsplanSerialized.class, id);
			mgr.remove(vertretungsplanserialized);
		} finally {
			mgr.close();
		}
	}

	private boolean containsVertretungsplanSerialized(
			VertretungsplanSerialized vertretungsplanserialized) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			VertretungsplanSerialized item = mgr.find(
					VertretungsplanSerialized.class,
					vertretungsplanserialized.getSchoolId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
