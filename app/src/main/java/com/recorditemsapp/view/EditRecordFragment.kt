package com.recorditemsapp.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.recorditemsapp.R
import com.recorditemsapp.core.TypeError
import com.recorditemsapp.databinding.FragmentEditRecordBinding
import com.recorditemsapp.model.entity.RecordEntity
import com.recorditemsapp.viewmodel.EditRecordViewModel

class EditRecordFragment : Fragment(R.layout.fragment_edit_record) {

    private lateinit var mBinding: FragmentEditRecordBinding
    private lateinit var mEditRecordViewModel: EditRecordViewModel
    private var mActivity: MainRecordActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mRecordEntity: RecordEntity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentEditRecordBinding.bind(view)
        mEditRecordViewModel = ViewModelProvider(requireActivity())[EditRecordViewModel::class.java]

        setupViewModel()
        setupTextFields()
    }

    private fun setupViewModel() {
        mEditRecordViewModel.getRecordSelected().observe(viewLifecycleOwner) {
            mRecordEntity = it ?: RecordEntity()
            if (it != null) {
                mIsEditMode = true
                setUiRecord(it)
            } else {
                mIsEditMode = false
            }
            setupActionBar()
        }

        mEditRecordViewModel.getResult().observe(viewLifecycleOwner) { result ->
            hideKeyboard()
            when (result) {
                is RecordEntity -> {
                    val msgRes = if (result.id == 0L) R.string.edit_record_message_save_success
                    else R.string.edit_record_message_update_success
                    mEditRecordViewModel.setRecordSelected(mRecordEntity)
                    Snackbar.make(mBinding.root,
                        msgRes,
                        Snackbar.LENGTH_SHORT).show()
                    mActivity?.onBackPressed()
                }
            }
        }

        mEditRecordViewModel.getTypeError().observe(viewLifecycleOwner) { typeError ->
            if (typeError != TypeError.NONE) {
                val msgRes = when (typeError) {
                    TypeError.GET -> R.string.main_error_get
                    TypeError.INSERT -> R.string.main_error_insert
                    TypeError.UPDATE -> R.string.main_error_update
                    TypeError.DELETE -> R.string.main_error_delete
                    else -> R.string.main_error_unknown
                }
                Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActionBar() {
        mActivity = activity as? MainRecordActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title =
            if (mIsEditMode) getString(R.string.edit_record_title_edit)
            else getString(R.string.edit_record_title_add)

        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {
        with(mBinding) {
            etName.addTextChangedListener { validateFields(tilName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun setUiRecord(recordEntity: RecordEntity) {
        with(mBinding) {
            etName.text = recordEntity.name.editable()
            etPhone.text = recordEntity.phone.editable()
            etWebsite.text = recordEntity.website.editable()
            etPhotoUrl.text = recordEntity.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if (validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)) {
                    with(mRecordEntity) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }
                    if (mIsEditMode) mEditRecordViewModel.updateRecord(mRecordEntity)
                    else mEditRecordViewModel.saveRecord(mRecordEntity)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else textField.error = null
        }
        if (!isValid) Snackbar.make(mBinding.root,
            R.string.edit_record_message_valid,
            Snackbar.LENGTH_SHORT).show()
        return isValid
    }

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditRecordViewModel.setResult(Any())
        mEditRecordViewModel.setTypeError(TypeError.NONE)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}