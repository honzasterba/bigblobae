/*
 * Licensed under Creative Commons Attribution License 3.0
 * 
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Shall be used for good.
 * 
 * (c) Copyright Jan Sterba 2010
 * http://jansterba.com
 */
package org.honzasterba.bigblobae;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * A piece of a BigBlob that fits into the storage limits
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BigBlobFragment {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	/**
	 * Actual data of this fragment
	 */
	@Persistent
	private Blob data;

	/**
	 * @param byteData
	 *            Actual data to store, cannot be null
	 * @throws IllegalArgumentException
	 *             in case byteData is null
	 */
	public BigBlobFragment(byte[] byteData) {
		if (byteData == null) {
			throw new IllegalArgumentException("Data cannot be null.");
		}
		data = new Blob(byteData);
	}

	/**
	 * @return Data holder for this fragment
	 */
	public Blob getData() {
		return data;
	}

}
