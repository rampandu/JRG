package com.ram.farmersmarket.fragments

/*
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ProductAdapter
    private val products = mutableListOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadProducts()
        setupSearch()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(products) { product ->
            // Handle product click
            showProductDetails(product)
        }
        binding.recyclerViewProducts.adapter = adapter
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadProducts() {
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("status", "available")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error loading products", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                products.clear()
                value?.documents?.forEach { document ->
                    val product = document.toObject(Product::class.java)
                    product?.let { products.add(it) }
                }
                adapter.notifyDataSetChanged()

                if (products.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                } else {
                    binding.emptyState.visibility = View.GONE
                }
            }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterProducts(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterProducts(query: String) {
        // Implement search logic
    }

    private fun showProductDetails(product: Product) {
        val dialog = ProductDetailDialog(requireContext(), product)
        dialog.show()
    }
}


 */