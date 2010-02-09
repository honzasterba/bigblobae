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

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * A BigBlob is a container that allows to store unlimited amounts of data into
 * a single object under Google AppEngine. Currently the AppEngine data store
 * has limit of one megabyte per row. This class helps to work around that limit
 * by fragmenting data into more smaller rows.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BigBlob {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	/**
	 * Arbitrary constant that guarantees that any records created by this class
	 * will not violate the AppEngine JDO store limits.
	 */
	public static final int FRAGMENT_LIMIT = 777 * 1024;

	/*
	 * This list gets serialized and then restored by the JDO store.
	 */
	@Persistent
	private List<BigBlobFragment> fragments = new ArrayList<BigBlobFragment>();

	/**
	 * Push the data to the store.
	 * 
	 * @param data
	 *            data to be stored, cannot be null.
	 */
	public BigBlob(final byte[] data) {
		int written = 0;
		while (written < data.length) {
			final int segmentLength = Math.min(FRAGMENT_LIMIT, data.length
					- written);
			final byte[] segmentData = new byte[segmentLength];
			System.arraycopy(data, written, segmentData, 0, segmentLength);
			fragments.add(new BigBlobFragment(segmentData));
			written += segmentLength;
		}
	}

	/**
	 * Get the data from the store, just like you left them.
	 * 
	 * @return the data from the store
	 */
	public byte[] getData() {
		if (fragments.size() == 0) {
			return new byte[0];
		} else if (fragments.size() == 1) {
			return fragments.get(0).getData().getBytes();
		} else {
			return concatFragments();
		}
	}

	/*
	 * Concatenate the fragmented data into one big byte array
	 */
	private byte[] concatFragments() {
		int length = 0;
		for (BigBlobFragment f : fragments) {
			length += f.getData().getBytes().length;
		}
		byte[] result = new byte[length];
		int written = 0;
		for (BigBlobFragment f : fragments) {
			System.arraycopy(f.getData().getBytes(), 0, result, written, f
					.getData().getBytes().length);
			written += f.getData().getBytes().length;
		}
		return result;
	}

}
