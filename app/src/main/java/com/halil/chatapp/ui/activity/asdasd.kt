
//
//                val dialogView = layoutInflater.inflate(R.layout.alert_dialog_pick_file, null)
//                val selectFileButton = dialogView.findViewById<Button>(R.id.select_file_notes)
//                selectFileButton.setOnClickListener {
//                    openFilePicker()
//                }
//                val builder = AlertDialog.Builder(requireContext())
//                builder.setView(dialogView)
//                builder.setPositiveButton(R.string.ok) { dialog, _ ->
//                    val fileUri = selectedFileUri
//                    if (fileUri == null) {
//                        Toast.makeText(
//                            requireContext(), R.string.pickFile, Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        uploadFile(fileUri)
//                    }
//                    dialog.dismiss()
//                }
//                val dialog = builder.create()
//                dialog.show()