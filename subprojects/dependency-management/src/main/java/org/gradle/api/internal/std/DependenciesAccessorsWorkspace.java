/*
 * Copyright 2020 the original author or authors.
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
package org.gradle.api.internal.std;

import org.gradle.api.internal.cache.StringInterner;
import org.gradle.cache.CacheRepository;
import org.gradle.cache.internal.CacheScopeMapping;
import org.gradle.cache.internal.InMemoryCacheDecoratorFactory;
import org.gradle.cache.internal.VersionStrategy;
import org.gradle.initialization.layout.ProjectCacheDir;
import org.gradle.internal.execution.history.ExecutionHistoryStore;
import org.gradle.internal.execution.workspace.WorkspaceProvider;
import org.gradle.internal.execution.workspace.impl.DefaultImmutableWorkspaceProvider;
import org.gradle.internal.file.FileAccessTimeJournal;

import java.io.Closeable;
import java.util.Optional;

public class DependenciesAccessorsWorkspace implements WorkspaceProvider, Closeable {
    private final DefaultImmutableWorkspaceProvider delegate;

    public DependenciesAccessorsWorkspace(ProjectCacheDir projectCacheDir, CacheScopeMapping cacheScopeMapping, CacheRepository cacheRepository, FileAccessTimeJournal fileAccessTimeJournal, InMemoryCacheDecoratorFactory inMemoryCacheDecoratorFactory, StringInterner stringInterner) {
        this.delegate = DefaultImmutableWorkspaceProvider.withBuiltInHistory(
            cacheRepository
                .cache(cacheScopeMapping.getBaseDirectory(projectCacheDir.getDir(), "dependencies-accessors", VersionStrategy.CachePerVersion))
                .withDisplayName("dependencies-accessors"),
            fileAccessTimeJournal,
            inMemoryCacheDecoratorFactory,
            stringInterner);
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
