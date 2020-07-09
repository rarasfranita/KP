package com.example.lotus.models

import androidx.annotation.IntDef

class CommentRowModel {

    companion object {

        @IntDef(PARENT, CHILD)
        @Retention(AnnotationRetention.SOURCE)
        annotation class RowType

        const val PARENT = 1
        const val CHILD = 2
    }

    @RowType var type : Int

    lateinit var parent : Comment

    lateinit var child : ChildComment

    var isExpanded : Boolean

    constructor(@RowType type : Int, parent : Comment, isExpanded : Boolean = false){
        this.type = type
        this.parent = parent
        this.isExpanded = isExpanded
    }

    constructor(@RowType type : Int, child : ChildComment, isExpanded : Boolean = false){
        this.type = type
        this.child = child
        this.isExpanded = isExpanded
    }

}