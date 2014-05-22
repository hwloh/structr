/**
 * Copyright (C) 2010-2014 Morgner UG (haftungsbeschränkt)
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.property;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.SortField;
import org.neo4j.index.lucene.ValueContext;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.app.Query;
import org.structr.core.converter.PropertyConverter;
import org.structr.core.graph.search.IntegerSearchAttribute;
import org.structr.core.graph.search.SearchAttribute;
import static org.structr.core.property.IntProperty.INT_EMPTY_FIELD_VALUE;

/**
 * A read-only property that returns the number of elements in a collection returned from a given property.
 *
 * @author Christian Morgner
 */
public class ElementCounter extends AbstractReadOnlyProperty<Integer> {

	private Property<? extends Iterable> collectionProperty = null;

	public ElementCounter(String name, Property<? extends Iterable> collectionProperty) {
		super(name);

		this.collectionProperty = collectionProperty;
	}

	@Override
	public Integer getProperty(SecurityContext securityContext, GraphObject obj, boolean applyConverter) {
		return getProperty(securityContext, obj, applyConverter, null);
	}

	@Override
	public Integer getProperty(SecurityContext securityContext, GraphObject obj, boolean applyConverter, final org.neo4j.helpers.Predicate<GraphObject> predicate) {

		int count = 0;

		if(obj != null) {

			Object toCount = obj.getProperty(collectionProperty);
			if(toCount != null) {

				if (toCount instanceof Collection) {

					count = ((Collection)toCount).size();

				} else if (toCount instanceof Iterable) {

					for(Object o : ((Iterable)toCount)) {
						count++;
					}

				} else {

					// a single object
					count = 1;
				}
			}
		}

		return count;
	}

	@Override
	public Class relatedType() {
		return null;
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public Integer getSortType() {
		return SortField.INT;
	}

	@Override
	public PropertyConverter<?, Integer> inputConverter(SecurityContext securityContext) {
		return new InputConverter(securityContext);
	}

	protected class InputConverter extends PropertyConverter<Object, Integer> {

		public InputConverter(SecurityContext securityContext) {
			super(securityContext, null);
		}

		@Override
		public Object revert(Integer source) throws FrameworkException {
			return source;
		}

		@Override
		public Integer convert(Object source) {

			if (source == null) return null;

			if (source instanceof Number) {

				return ((Number)source).intValue();

			}

			if (source instanceof String && StringUtils.isNotBlank((String) source)) {

				return Integer.parseInt(source.toString());
			}

			return null;

		}
	}

	@Override
	public SearchAttribute getSearchAttribute(SecurityContext securityContext, BooleanClause.Occur occur, Integer searchValue, boolean exactMatch, final Query query) {
		return new IntegerSearchAttribute(this, searchValue, occur, exactMatch);
	}

	@Override
	public void index(GraphObject entity, Object value) {
		super.index(entity, value != null ? ValueContext.numeric((Number)value) : value);
	}

	@Override
	public String getValueForEmptyFields() {
		return INT_EMPTY_FIELD_VALUE;
	}

}
