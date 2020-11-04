/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.transform;

import com.google.common.collect.ImmutableList;
import org.gradle.cache.CacheBuilder;
import org.gradle.cache.internal.CrossBuildInMemoryCache;
import org.gradle.internal.Try;
import org.gradle.internal.execution.UnitOfWork;
import org.gradle.internal.execution.history.ExecutionHistoryStore;
import org.gradle.internal.execution.workspace.impl.DefaultImmutableWorkspaceProvider;
import org.gradle.internal.file.FileAccessTimeJournal;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Closeable;
import java.io.File;
import java.util.Optional;

@NotThreadSafe
public class ImmutableTransformationWorkspaceProvider implements TransformationWorkspaceProvider, Closeable {
    private final CrossBuildInMemoryCache<UnitOfWork.Identity, Try<ImmutableList<File>>> identityCache;
    private final DefaultImmutableWorkspaceProvider delegate;

    public ImmutableTransformationWorkspaceProvider(
        CacheBuilder cacheBuilder,
        FileAccessTimeJournal fileAccessTimeJournal,
        ExecutionHistoryStore executionHistoryStore,
        CrossBuildInMemoryCache<UnitOfWork.Identity, Try<ImmutableList<File>>> identityCache
    ) {
        this.delegate = DefaultImmutableWorkspaceProvider.withExternalHistory(cacheBuilder, fileAccessTimeJournal, executionHistoryStore);
        this.identityCache = identityCache;
    }

    @Override
    public CrossBuildInMemoryCache<UnitOfWork.Identity, Try<ImmutableList<File>>> getIdentityCache() {
        return identityCache;
    }

    @Override
    public <T> T withWorkspace(String path, WorkspaceAction<T> action) {
        return delegate.withWorkspace(path, action);
    }

    @Override
    public Optional<ExecutionHistoryStore> getHistory() {
        return delegate.getHistory();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
