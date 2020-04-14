/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.basicdataset;

import ai.djl.modality.nlp.embedding.EmbeddingException;
import ai.djl.modality.nlp.preprocess.SimpleTokenizer;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.dataset.Record;
import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TatoebaEnglishFrenchDatasetTest {

    private static final int EMBEDDING_SIZE = 15;

    @Test
    public void testGetDataWithPreTrainedEmbedding() throws IOException, EmbeddingException {
        try (NDManager manager = NDManager.newBaseManager()) {
            TatoebaEnglishFrenchDataset tatoebaEnglishFrenchDataset =
                    TatoebaEnglishFrenchDataset.builder()
                            .optSourceTextEmbedding(
                                    TestUtils.getTextEmbedding(manager, EMBEDDING_SIZE), false)
                            .optTargetTextEmbedding(
                                    TestUtils.getTextEmbedding(manager, EMBEDDING_SIZE), false)
                            .setTokenizer(new SimpleTokenizer())
                            .setValidLength(true)
                            .setSampling(32, true)
                            .build();
            tatoebaEnglishFrenchDataset.prepare();
            Record record = tatoebaEnglishFrenchDataset.get(manager, 0);
            Assert.assertEquals(record.getData().get(0).getShape().dimension(), 2);
            Assert.assertEquals(record.getData().get(0).getShape().get(1), EMBEDDING_SIZE);
            Assert.assertEquals(record.getData().get(1).getShape(), new Shape());
            Assert.assertEquals(record.getLabels().get(0).getShape().dimension(), 2);
            Assert.assertEquals(record.getLabels().get(0).getShape().get(1), EMBEDDING_SIZE);
            Assert.assertEquals(record.getLabels().get(1).getShape(), new Shape());
        }
    }

    @Test
    public void testGetDataWithTrainableEmbedding() throws IOException, EmbeddingException {
        try (NDManager manager = NDManager.newBaseManager()) {
            TatoebaEnglishFrenchDataset tatoebaEnglishFrenchDataset =
                    TatoebaEnglishFrenchDataset.builder()
                            .optEmbeddingSize(EMBEDDING_SIZE)
                            .setTokenizer(new SimpleTokenizer())
                            .setValidLength(false)
                            .setSampling(32, true)
                            .build();
            tatoebaEnglishFrenchDataset.prepare();
            Record record = tatoebaEnglishFrenchDataset.get(manager, 0);
            Assert.assertEquals(record.getData().get(0).getShape(), new Shape(10));
            Assert.assertEquals(record.getData().size(), 1);
            Assert.assertEquals(record.getLabels().get(0).getShape(), new Shape(12));
            Assert.assertEquals(record.getLabels().size(), 1);
        }
    }
}