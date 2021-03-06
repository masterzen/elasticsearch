/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.mapper.xcontent.index;

import org.apache.lucene.document.Field;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.mapper.IndexFieldMapper;
import org.elasticsearch.index.mapper.ParsedDocument;
import org.elasticsearch.index.mapper.xcontent.MapperTests;
import org.elasticsearch.index.mapper.xcontent.XContentDocumentMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author kimchy (shay.banon)
 */
public class IndexTypeMapperTests {

    @Test public void simpleIndexMapperTests() throws Exception {
        String mapping = XContentFactory.jsonBuilder().startObject().startObject("type")
                .startObject("_index").field("enabled", true).field("store", "yes").endObject()
                .endObject().endObject().string();
        XContentDocumentMapper docMapper = MapperTests.newParser().parse(mapping);
        assertThat(docMapper.indexMapper().enabled(), equalTo(true));
        assertThat(docMapper.indexMapper().store(), equalTo(Field.Store.YES));
        assertThat(docMapper.mappers().indexName("_index").mapper(), instanceOf(IndexFieldMapper.class));

        ParsedDocument doc = docMapper.parse("type", "1", XContentFactory.jsonBuilder()
                .startObject()
                .field("field", "value")
                .endObject()
                .copiedBytes());

        assertThat(doc.doc().get("_index"), equalTo("test"));
        assertThat(doc.doc().get("field"), equalTo("value"));
    }

    @Test public void explicitDisabledIndexMapperTests() throws Exception {
        String mapping = XContentFactory.jsonBuilder().startObject().startObject("type")
                .startObject("_index").field("enabled", false).field("store", "yes").endObject()
                .endObject().endObject().string();
        XContentDocumentMapper docMapper = MapperTests.newParser().parse(mapping);
        assertThat(docMapper.indexMapper().enabled(), equalTo(false));
        assertThat(docMapper.indexMapper().store(), equalTo(Field.Store.YES));

        ParsedDocument doc = docMapper.parse("type", "1", XContentFactory.jsonBuilder()
                .startObject()
                .field("field", "value")
                .endObject()
                .copiedBytes());

        assertThat(doc.doc().get("_index"), nullValue());
        assertThat(doc.doc().get("field"), equalTo("value"));
    }

    @Test public void defaultDisabledIndexMapperTests() throws Exception {
        String mapping = XContentFactory.jsonBuilder().startObject().startObject("type")
                .endObject().endObject().string();
        XContentDocumentMapper docMapper = MapperTests.newParser().parse(mapping);
        assertThat(docMapper.indexMapper().enabled(), equalTo(false));
        assertThat(docMapper.indexMapper().store(), equalTo(Field.Store.NO));

        ParsedDocument doc = docMapper.parse("type", "1", XContentFactory.jsonBuilder()
                .startObject()
                .field("field", "value")
                .endObject()
                .copiedBytes());

        assertThat(doc.doc().get("_index"), nullValue());
        assertThat(doc.doc().get("field"), equalTo("value"));
    }
}
