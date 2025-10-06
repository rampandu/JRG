package com.ram.farmersmarket.adapters
/*
class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivProduct)
        private val titleView: TextView = itemView.findViewById(R.id.tvTitle)
        private val priceView: TextView = itemView.findViewById(R.id.tvPrice)
        private val locationView: TextView = itemView.findViewById(R.id.tvLocation)
        private val sellerView: TextView = itemView.findViewById(R.id.tvSeller)

        fun bind(product: Product) {
            titleView.text = product.title
            priceView.text = "₹${product.price}"
            locationView.text = product.location
            sellerView.text = product.sellerName

            if (product.imageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView)
            }

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }
}

 */