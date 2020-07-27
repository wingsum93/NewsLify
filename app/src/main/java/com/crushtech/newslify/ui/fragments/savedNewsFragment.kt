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
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class savedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: SavedArticlesAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = (activity as NewsActivity).newsViewModel
        retainInstance = true
        setUpRecyclerView()


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

