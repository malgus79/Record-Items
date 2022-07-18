package com.recorditemsapp.view.adapter

import com.recorditemsapp.model.entity.RecordEntity

interface OnClickListener {
    fun onClick(recordEntity: RecordEntity)
    fun onFavoriteRecord(recordEntity: RecordEntity)
    fun onDeleteRecord(recordEntity: RecordEntity)
}