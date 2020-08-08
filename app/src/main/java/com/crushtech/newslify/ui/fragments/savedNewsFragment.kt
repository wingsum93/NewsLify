package com.crushtech.newslify.ui.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.R
import com.crushtech.newslify.adapter.SavedArticlesAdapter
import com.crushtech.newslify.models.Article
import com.crushtech.newslify.models.SimpleCustomSnackbar
import com.crushtech.newslify.ui.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class savedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: SavedArticlesAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = (activity as NewsActivity).newsViewModel
        retainInstance = true
        setUpRecyclerView()
        setupSpannerLikeFilter()
        setUpSpinnerLikeForCategories()


        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)

                val snackListener = View.OnClickListener {
                    viewModel.saveArticle(article)
                }
                SimpleCustomSnackbar.make(
                    view, "article deleted",
                    Snackbar.LENGTH_LONG, snackListener, R.drawable.delete_article, "Undo",
                    null
                )?.show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                ).addBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_red_AA))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("delete article")
                    .setSwipeLeftLabelTextSize(1, 18F)
                    .setSwipeLeftLabelColor(R.color.white)
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
            newsAdapter.differ.submitList(article)
            updateUI(article)
        })
        setHasOptionsMenu(true)
    }

    private fun setupSpannerLikeFilter() {
        val spinnerBottomDialog = BottomSheetDialog(
            requireContext(),
            R.style.Theme_MaterialComponents_BottomSheetDialog
        )
        val view =
            LayoutInflater.from(context).inflate(R.layout.saved_items_spinner, null)
        spinnerBottomDialog.setContentView(view)
        val allSavedItems = view.findViewById<TextView>(R.id.All_saved_items)
        val oldestSavedItems = view.findViewById<TextView>(R.id.oldest_saved_items)
        val newestSavedItems = view.findViewById<TextView>(R.id.newest_saved_items)
        val ascSavedItems = view.findViewById<TextView>(R.id.asc_saved_items)
        val cancelDialog = view.findViewById<MaterialButton>(R.id.cancel_filter)

        allSavedItems.setOnClickListener {
            spinnerFilter.text = allSavedItems.text
            allSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.email_sent_icon,
                0
            )
            oldestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            ascSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            newestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

            spinnerBottomDialog.dismiss()
            //  allSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                newsAdapter.differ.submitList(article)
                updateUI(article)
            })
        }
        oldestSavedItems.setOnClickListener {
            spinnerFilter.text = oldestSavedItems.text
            oldestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.email_sent_icon,
                0
            )
            allSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            ascSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            newestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

            spinnerBottomDialog.dismiss()
            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                newsAdapter.differ.submitList(article)
                updateUI(article)
            })
        }
        newestSavedItems.setOnClickListener {
            spinnerFilter.text = newestSavedItems.text

            newestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.email_sent_icon,
                0
            )
            oldestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            ascSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            allSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)


            spinnerBottomDialog.dismiss()
            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                newsAdapter.differ.submitList(article.reversed())
                updateUI(article)
            })
        }
        ascSavedItems.setOnClickListener {
            spinnerFilter.text = ascSavedItems.text

            ascSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.email_sent_icon,
                0
            )
            oldestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            allSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            newestSavedItems.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

            spinnerBottomDialog.dismiss()
            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val ascOrder = article.sortedBy {
                    it.title
                }
                newsAdapter.differ.submitList(ascOrder)
                updateUI(article)
            })
        }

        cancelDialog.setOnClickListener {
            spinnerBottomDialog.dismiss()
        }
        spinnerFilter.setOnClickListener {
            spinnerBottomDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            spinnerBottomDialog.show()
        }
    }

    private fun setUpSpinnerLikeForCategories() {
        val spinnerBottomDialog = BottomSheetDialog(
            requireContext(),
            R.style.Theme_MaterialComponents_BottomSheetDialog
        )
        val view =
            LayoutInflater.from(context).inflate(R.layout.saved_items_spinner_categories, null)
        spinnerBottomDialog.setContentView(view)

        val tech = view.findViewById<TextView>(R.id.tech_saved_items)
        val gen = view.findViewById<TextView>(R.id.gen_saved_items)
        val sport = view.findViewById<TextView>(R.id.sport_saved_items)
        val bus = view.findViewById<TextView>(R.id.bus_saved_items)
        val sci = view.findViewById<TextView>(R.id.science_saved_items)
        val health = view.findViewById<TextView>(R.id.health_saved_items)
        val enter = view.findViewById<TextView>(R.id.ent_saved_items)
        val dismissDialog = view.findViewById<MaterialButton>(R.id.cancel_category)


        tech.setOnClickListener {
            spinnerFilter1.text = tech.text
            spinnerBottomDialog.dismiss()
            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val techNews = article.filter {
                    it.category!!.contains("Technology")
                }
                noCategoryFoundErrorMessage(techNews)
                newsAdapter.differ.submitList(techNews)
            })
        }
        gen.setOnClickListener {
            spinnerFilter1.text = gen.text
            spinnerBottomDialog.dismiss()
            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val genNews = article.filter {
                    it.category!!.contains("General")
                }
                noCategoryFoundErrorMessage(genNews)
                newsAdapter.differ.submitList(genNews)
            })
        }
        sport.setOnClickListener {
            spinnerFilter1.text = sport.text
            spinnerBottomDialog.dismiss()

            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val sportNews = article.filter {
                    it.category!!.contains("Sports")
                }
                noCategoryFoundErrorMessage(sportNews)
                newsAdapter.differ.submitList(sportNews)
            })
        }
        bus.setOnClickListener {
            spinnerFilter1.text = bus.text
            spinnerBottomDialog.dismiss()

            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val busNews = article.filter {
                    it.category!!.contains("Business")
                }
                noCategoryFoundErrorMessage(busNews)
                newsAdapter.differ.submitList(busNews)
            })
        }
        sci.setOnClickListener {
            spinnerFilter1.text = sci.text
            spinnerBottomDialog.dismiss()

            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val sciNews = article.filter {
                    it.category!!.contains("Science")
                }
                noCategoryFoundErrorMessage(sciNews)
                newsAdapter.differ.submitList(sciNews)
            })
        }
        health.setOnClickListener {
            spinnerFilter1.text = health.text
            spinnerBottomDialog.dismiss()

            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val healthNews = article.filter {
                    it.category!!.contains("Health")
                }
                noCategoryFoundErrorMessage(healthNews)
                newsAdapter.differ.submitList(healthNews)
            })
        }
        enter.setOnClickListener {
            spinnerFilter1.text = enter.text
            spinnerBottomDialog.dismiss()

            viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
                val enterNews = article.filter {
                    it.category!!.contains("Entertainment")
                }
                noCategoryFoundErrorMessage(enterNews)
                newsAdapter.differ.submitList(enterNews)
            })
        }

        dismissDialog.setOnClickListener {
            spinnerBottomDialog.dismiss()
        }
        spinnerFilter1.setOnClickListener {
            when (spinnerFilter1.text) {
                "General" -> {
                    gen.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.email_sent_icon,
                        0
                    )
                    sport.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    tech.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sci.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    bus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    health.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    enter.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
                "Sport" -> {
                    sport.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.email_sent_icon,
                        0
                    )
                    tech.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sci.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    bus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    health.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    enter.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    gen.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
                "Technology" -> {
                    tech.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.email_sent_icon,
                        0
                    )
                    sci.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    bus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    health.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    enter.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    gen.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sport.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
                "Science" -> {
                    sci.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.email_sent_icon,
                        0
                    )
                    bus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    health.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    enter.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    gen.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sport.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    tech.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
                "Business" -> {
                    bus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.email_sent_icon,
                        0
                    )
                    health.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    enter.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    gen.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sport.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    tech.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sci.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
                "Health" -> {
                    health.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.email_sent_icon,
                        0
                    )
                    enter.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    gen.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sport.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    tech.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sci.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    bus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
                "Entertainment" -> {
                    enter.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.email_sent_icon,
                        0
                    )
                    gen.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sport.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    tech.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    sci.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    bus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    health.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
            spinnerBottomDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            spinnerBottomDialog.show()
        }
    }

    private fun noCategoryFoundErrorMessage(article: List<Article>) {
        if (article.isEmpty()) {
            SimpleCustomSnackbar.make(
                requireView(),
                "No saved news was found in this category",
                Snackbar.LENGTH_LONG, null, R.drawable.no_item_icon, "", null
            )?.show()
        }
        updateUI(article)
    }

    private fun setUpRecyclerView() {
        newsAdapter = SavedArticlesAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun updateUI(article: List<Article>) {
        val adapterHasNoItem = article.isEmpty()
        if (adapterHasNoItem) {
            lottie_no_article_saved.visibility = View.VISIBLE
            no_saved_article_text1.visibility = View.VISIBLE
            no_saved_article_text2.visibility = View.VISIBLE
        } else {
            lottie_no_article_saved.visibility = View.INVISIBLE
            no_saved_article_text1.visibility = View.INVISIBLE
            no_saved_article_text2.visibility = View.INVISIBLE
        }
    }

    override fun onAttach(context: Context) {
        (activity as NewsActivity).showBottomNavigation()
        super.onAttach(context)
    }

    override fun onResume() {
        (activity as NewsActivity).showBottomNavigation()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_all, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAllArticles -> {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.delete_all_dialog)
                val dismissDialog = dialog.findViewById<Button>(R.id.btn_no)
                val deleteAll = dialog.findViewById<Button>(R.id.btn_yes)
                val Anim: Animation = AnimationUtils.loadAnimation(
                    context,
                    android.R.anim.fade_in
                )

                dismissDialog.setOnClickListener {
                    dismissDialog.animation = Anim

                    dialog.dismiss()
                }
                deleteAll.setOnClickListener {
                    deleteAll.animation = Anim
                    val noSavedItem = newsAdapter.differ.currentList.isEmpty()
                    if (noSavedItem) {
                        view?.let { it1 ->
                            SimpleCustomSnackbar.make(
                                it1, "Sorry: no item saved!",
                                Snackbar.LENGTH_LONG, null, R.drawable.no_item_icon, "",
                                null
                            )?.show()
                        }
                    } else {
                        GlobalScope.launch(Dispatchers.IO) {
                            viewModel.deleteAllArticles()
                        }
                        view?.let { it1 ->
                            SimpleCustomSnackbar.make(
                                it1, "all saved articles deleted",
                                Snackbar.LENGTH_LONG, null, R.drawable.delete_article, "",
                                null
                            )?.show()
                        }
                    }
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                }
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                dialog.setCancelable(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}



