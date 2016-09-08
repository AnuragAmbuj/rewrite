package com.netflix.java.refactor.diff

import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.FileMode
import java.io.ByteArrayOutputStream

internal class InMemoryDiffEntry(filePath: String, old: String, new: String): DiffEntry() {
    private val repo = InMemoryRepository.Builder().build()
    
    init {
        changeType = ChangeType.MODIFY
        oldPath = filePath
        newPath = filePath

        val inserter = repo.objectDatabase.newInserter()
        oldId = inserter.insert(Constants.OBJ_BLOB, old.toByteArray()).abbreviate(40)
        newId = inserter.insert(Constants.OBJ_BLOB, new.toByteArray()).abbreviate(40)
        inserter.flush()

        oldMode = FileMode.REGULAR_FILE
        newMode = FileMode.REGULAR_FILE
        repo.close()
    }

    val diff: String by lazy {
        if(oldId == newId)
            ""
        else {
            val patch = ByteArrayOutputStream()
            val formatter = DiffFormatter(patch)
            formatter.setRepository(repo)
            formatter.format(this)
            String(patch.toByteArray())
        }
    }
}