package com.diplom.map.mvp.components.layerstyle.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diplom.map.MainActivity
import com.diplom.map.R
import com.diplom.map.mvp.App
import com.diplom.map.mvp.abstracts.view.BaseFragment
import com.diplom.map.mvp.components.layerstyle.contract.LayerStyleFragmentContract
import com.diplom.map.mvp.components.layerstyle.model.StyleRecyclerViewAdapter
import com.diplom.map.mvp.components.layerstyle.presenter.LayerStyleFragmentPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_page_layers_styles.*
import javax.inject.Inject

class LayerStyleFragment : BaseFragment(), LayerStyleFragmentContract.View {

    @Inject
    lateinit var presenter: LayerStyleFragmentPresenter

    override fun init(savedInstanceState: Bundle?) {
        App.get().injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_page_layers_styles, container, false)
        setupThemeSpinner(rootView)
        return rootView
    }


    private fun setupThemeSpinner(root: View) {
        val spinnerLayer = root.findViewById<Spinner>(R.id.spinnerLayer)
        val spinnerColumn = root.findViewById<Spinner>(R.id.spinnerColumn)
        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerStyle)
        presenter.disposable.add(presenter.getLayers().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (it.isNotEmpty()) {
                    ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, it)
                        .also { adapter ->
                            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                            spinnerLayer.adapter = adapter
                            setColumnSpinner(recyclerView, spinnerColumn, it[0])
                        }
                    spinnerLayer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            setColumnSpinner(recyclerView, spinnerColumn, it[position])
                        }
                    }
                }
            }
            .doOnError { Log.d("Hello", "Layer Spinner err: ${it.message}") }
            .subscribe()
        )
    }

    fun setColumnSpinner(recycler: RecyclerView, spinner: Spinner, layername: String) {
        presenter.disposable.add(presenter.getColumnNames(layername)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (it.isNotEmpty()) {
                    ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, it)
                        .also { adapter ->
                            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                            spinner.adapter = adapter
                            setupRecycler(recycler, layername, it[0])
                        }
                    spinnerColumn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            setupRecycler(recycler, layername, it[position])
                        }

                    }
                }
            }
            .doOnComplete { }
            .doOnError { Log.d("Hello", "Columnnamae spinner err: ${it.message}") }
            .subscribe()
        )
    }


    fun setupRecycler(recycler: RecyclerView, layerName: String, columnName: String) {
        presenter.disposable.add(presenter.getThemeValues(layerName, columnName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                recycler.layoutManager = LinearLayoutManager(this.context)
                recycler.adapter =
                    StyleRecyclerViewAdapter(
                        activity as MainActivity,
                        it.values.sortedWith(compareBy { value ->
                            value.value
                        }),
                        presenter.disposable
                    )
            }
            .doOnComplete { }
            .doOnError { }
            .subscribe()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }
}