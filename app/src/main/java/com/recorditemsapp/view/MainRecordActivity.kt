package com.recorditemsapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.recorditemsapp.R
import com.recorditemsapp.core.TypeError
import com.recorditemsapp.databinding.ActivityMainBinding
import com.recorditemsapp.model.entity.RecordEntity
import com.recorditemsapp.view.adapter.OnClickListener
import com.recorditemsapp.view.adapter.RecordListAdapter
import com.recorditemsapp.viewmodel.EditRecordViewModel
import com.recorditemsapp.viewmodel.MainRecordViewModel

class MainRecordActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: RecordListAdapter
    private lateinit var mGridLayout: GridLayoutManager
    private lateinit var mMainRecordViewModel: MainRecordViewModel
    private lateinit var mEditRecordViewModel: EditRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fab.setOnClickListener { launchEditRecordFragment() }

        setupViewModel()
        setupRecylcerView()
        //searchRecord()
    }

    private fun setupViewModel() {
        mMainRecordViewModel = ViewModelProvider(this)[MainRecordViewModel::class.java]
        mMainRecordViewModel.getRecords().observe(this) { stores ->
            mBinding.progressBar.visibility = View.GONE
            mAdapter.submitList(stores)
        }
        mMainRecordViewModel.isShowProgress().observe(this) { isShowProgress ->
            mBinding.progressBar.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
        mMainRecordViewModel.getTypeError().observe(this) { typeError ->
            val msgRes = when (typeError) {
                TypeError.GET -> R.string.main_error_get
                TypeError.INSERT -> R.string.main_error_insert
                TypeError.UPDATE -> R.string.main_error_update
                TypeError.DELETE -> R.string.main_error_delete
                else -> R.string.main_error_unknown
            }
            Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
        }

        mEditRecordViewModel = ViewModelProvider(this)[EditRecordViewModel::class.java]
        mEditRecordViewModel.getShowFab().observe(this) { isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        }
    }

    private fun launchEditRecordFragment(recordEntity: RecordEntity = RecordEntity()) {
        mEditRecordViewModel.setShowFab(false)
        mEditRecordViewModel.setRecordSelected(recordEntity)

        val fragment = EditRecordFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun setupRecylcerView() {
        mAdapter = RecordListAdapter(this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        mEditRecordViewModel.setShowFab(true)
    }

    override fun onClick(recordEntity: RecordEntity) {
        launchEditRecordFragment(recordEntity)
    }

    override fun onFavoriteRecord(recordEntity: RecordEntity) {
        mMainRecordViewModel.updateRecord(recordEntity)
    }

    override fun onDeleteRecord(recordEntity: RecordEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, i ->
                when (i) {
                    0 -> confirmDelete(recordEntity)
                    1 -> dial(recordEntity.phone)
                    2 -> goToWebsite(recordEntity.website)
                }
            }
            .show()
    }

    private fun confirmDelete(recordEntity: RecordEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                mMainRecordViewModel.deleteRecords(recordEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun dial(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWebsite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        }
    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }

//    private fun searchRecord() {
//        mBinding.searchIcon.setOnClickListener {
//            val intent = Intent(this@MainRecordActivity, SearchRecord::class.java)
//            startActivity(intent)
//        }
//    }
}