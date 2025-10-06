package com.ram.farmersmarket.fragments
/*
class SellFragment : Fragment() {

    private lateinit var binding: FragmentSellBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupCategorySpinner()
    }

    private fun setupClickListeners() {
        binding.btnSelectImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                uploadImageAndCreateProduct()
            }
        }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Livestock", "Crops", "Equipment", "Seeds", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.ivProductImage.setImageURI(imageUri)
            binding.ivProductImage.visibility = View.VISIBLE
        }
    }

    private fun uploadImageAndCreateProduct() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        // Get user data first
        FirebaseFirestore.getInstance().collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let { createProduct(it) }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createProduct(user: User) {
        val productId = FirebaseFirestore.getInstance().collection("products").document().id

        if (imageUri != null) {
            // Upload image first
            val storageRef = FirebaseStorage.getInstance().reference
                .child("product_images/${productId}")

            storageRef.putFile(imageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result.toString()
                        saveProductToFirestore(productId, user, downloadUrl)
                    } else {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            saveProductToFirestore(productId, user, "")
        }
    }

    private fun saveProductToFirestore(productId: String, user: User, imageUrl: String) {
        val product = Product(
            id = productId,
            title = binding.etTitle.text.toString(),
            description = binding.etDescription.text.toString(),
            price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0,
            category = binding.spinnerCategory.selectedItem.toString(),
            imageUrl = imageUrl,
            sellerId = user.id,
            sellerName = user.name,
            sellerPhone = user.phoneNumber,
            location = user.location
        )

        FirebaseFirestore.getInstance().collection("products")
            .document(productId)
            .set(product)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Product listed successfully!", Toast.LENGTH_SHORT).show()
                clearForm()
                (requireActivity() as MainActivity).loadHomeFragment()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to list product", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInputs(): Boolean {
        return binding.etTitle.text.toString().isNotEmpty() &&
                binding.etDescription.text.toString().isNotEmpty() &&
                binding.etPrice.text.toString().isNotEmpty() &&
                binding.spinnerCategory.selectedItemPosition > 0
    }

    private fun clearForm() {
        binding.etTitle.text.clear()
        binding.etDescription.text.clear()
        binding.etPrice.text.clear()
        binding.spinnerCategory.setSelection(0)
        binding.ivProductImage.visibility = View.GONE
        imageUri = null
    }
}

 */