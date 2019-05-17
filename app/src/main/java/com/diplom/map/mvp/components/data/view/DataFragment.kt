package com.diplom.map.mvp.components.data.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diplom.map.R
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.data.contract.DataFragmentContract
import com.diplom.map.mvp.components.data.model.Cell
import com.diplom.map.mvp.components.data.model.ColumnHeader
import com.diplom.map.mvp.components.data.model.MyTableViewAdapter
import com.diplom.map.mvp.components.data.model.RowHeader
import com.evrencoskun.tableview.TableView

class DataFragment : BaseFragment(), DataFragmentContract.View {

    override fun init(savedInstanceState: Bundle?) {

    }

    private lateinit var tableView: TableView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_page_data, container, false)
        val adapter = MyTableViewAdapter(this.context)
        tableView = rootView.findViewById(R.id.dataTable)
        tableView.adapter = adapter
        val colums = arrayListOf(
            ColumnHeader("Hello"),
            ColumnHeader("Hello 2"),
            ColumnHeader("Hello 3")
        )
        val rows = arrayListOf(
            RowHeader("1"),
            RowHeader("2"),
            RowHeader("3")
        )
        val cells = arrayListOf(
            arrayListOf(Cell("1"), Cell("2"), Cell("3")),
            arrayListOf(Cell("4"), Cell("5"), Cell("6")),
            arrayListOf(Cell("7"), Cell("8"), Cell("9"))
        )
        adapter.setAllItems(colums, rows, cells as List<List<Cell>>)
        return rootView
    }
}