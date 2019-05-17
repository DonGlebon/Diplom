package com.diplom.map.mvp.components.data.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.map.R
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import kotlinx.android.synthetic.main.table_cell.view.*

class MyTableViewAdapter(private val context: Context) :
    AbstractTableAdapter<ColumnHeader, RowHeader, Cell>(context) {


    class CellViewHolder(view: View) : AbstractViewHolder(view)

    override fun onCreateCellViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.table_cell, parent, false)
        return CellViewHolder(view)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder?,
        cellItemModel: Any?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        cellItemModel as Cell
        holder as CellViewHolder
        holder.itemView.Edit.text = cellItemModel.value
    }

    class ColumnViewHolder(view: View) : AbstractViewHolder(view)

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.table_column, parent, false)
        return ColumnViewHolder(view)
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder?,
        columnHeaderItemModel: Any?,
        columnPosition: Int
    ) {
        columnHeaderItemModel as ColumnHeader
        holder as ColumnViewHolder
        holder.itemView.Edit.text = columnHeaderItemModel.columnName
    }

    class RowViewHolder(view: View) : AbstractViewHolder(view)

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup?, viewType: Int): AbstractViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.table_row, parent, false)
        return RowViewHolder(view)
    }

    override fun onBindRowHeaderViewHolder(holder: AbstractViewHolder?, rowHeaderItemModel: Any?, rowPosition: Int) {
        rowHeaderItemModel as RowHeader
        holder as RowViewHolder
        holder.itemView.Edit.text = rowHeaderItemModel.rowId
    }

    override fun onCreateCornerView(): View {
        return LayoutInflater.from(context).inflate(R.layout.table_corner, null)
    }

    override fun getCellItemViewType(position: Int): Int {
        return 0
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return 0
    }
}