package com.project.farmingapp.view.apmc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.farmingapp.adapter.ApmcAdapter
import com.project.farmingapp.databinding.FragmentApmcBinding
import com.project.farmingapp.model.APMCApi
import com.project.farmingapp.model.data.APMCCustomRecords
import com.project.farmingapp.model.data.APMCMain
import com.project.farmingapp.model.data.APMCRecords
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApmcFragment : Fragment() {

    private lateinit var adapter: ApmcAdapter
    private lateinit var stateDistricts: LinkedHashMap<String, Array<String>>
    private var selectedState: String = "None"

    private var _binding: FragmentApmcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApmcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "APMC"

        stateDistricts = buildStateDistricts()
        binding.dateValueTextApmc.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.recycleAPMC.layoutManager = LinearLayoutManager(requireContext())
        setLoading(false)
        showMessage("Please select state and district")
        setupSpinners()
    }

    private fun setupSpinners() {
        val stateAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            stateDistricts.keys.toList()
        )
        binding.spinner1.adapter = stateAdapter
        binding.spinner2.adapter = districtAdapter(arrayOf("None"))

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedState = stateDistricts.keys.elementAt(position)
                val districts = stateDistricts[selectedState] ?: arrayOf("None")
                binding.spinner2.adapter = districtAdapter(districts)

                if (selectedState == "None") {
                    showMessage("Please select state and district")
                } else if (districts.size == 1) {
                    showMessage("District data for $selectedState is not configured yet")
                }
            }
        }

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val district = parent?.getItemAtPosition(position)?.toString().orEmpty()
                if (district == "None" || district.isBlank()) {
                    if (selectedState != "None") showMessage("Please select district")
                    return
                }

                getApmc(district)
            }
        }
    }

    private fun districtAdapter(districts: Array<String>): ArrayAdapter<String> {
        return ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            districts
        )
    }

    private fun getApmc(district: String) {
        setLoading(true)
        binding.textAPMCWarning.visibility = View.GONE

        APMCApi.apmcInstances.getSomeData(district).enqueue(object : Callback<APMCMain> {
            override fun onFailure(call: Call<APMCMain>, t: Throwable) {
                Log.e("APMC", "Live data unavailable", t)
                if (_binding != null) showFallbackData(district, "Live market API is unavailable. Showing sample prices.")
            }

            override fun onResponse(call: Call<APMCMain>, response: Response<APMCMain>) {
                if (_binding == null) return
                
                val apmcData = response.body()
                val records = apmcData?.records.orEmpty()

                if (!response.isSuccessful || records.isEmpty()) {
                    showFallbackData(district, "No live records found. Showing sample prices.")
                    return
                }

                binding.dateValueTextApmc.text = formatApiDate(apmcData?.updated_date)
                showRecords(groupRecords(records))
            }
        })
    }

    private fun showRecords(records: List<APMCCustomRecords>) {
        if (_binding == null) return
        setLoading(false)
        binding.textAPMCWarning.visibility = View.GONE
        binding.recycleAPMC.visibility = View.VISIBLE
        adapter = ApmcAdapter(requireContext(), records)
        binding.recycleAPMC.adapter = adapter
    }

    private fun showFallbackData(district: String, message: String) {
        if (_binding == null) return
        binding.dateValueTextApmc.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.textAPMCWarning.text = message
        binding.textAPMCWarning.visibility = View.VISIBLE
        binding.recycleAPMC.visibility = View.VISIBLE
        showRecords(sampleRecords(district))
        binding.textAPMCWarning.visibility = View.VISIBLE
    }

    private fun showMessage(message: String) {
        if (_binding == null) return
        setLoading(false)
        binding.textAPMCWarning.text = message
        binding.textAPMCWarning.visibility = View.VISIBLE
        binding.recycleAPMC.visibility = View.GONE
    }

    private fun setLoading(isLoading: Boolean) {
        if (_binding == null) return
        binding.progressApmc.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loadingTextAPMC.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun groupRecords(records: List<APMCRecords>): List<APMCCustomRecords> {
        return records.groupBy { it.market.ifBlank { "Local Market" } }
            .map { (_, marketRecords) ->
                val first = marketRecords.first()
                APMCCustomRecords(
                    first.state.ifBlank { selectedState },
                    first.district,
                    first.market.ifBlank { "Local Market" },
                    marketRecords.map { it.commodity.ifBlank { "Crop" } }.toMutableList(),
                    marketRecords.map { it.min_price.ifBlank { "-" } }.toMutableList(),
                    marketRecords.map { it.max_price.ifBlank { "-" } }.toMutableList()
                )
            }
    }

    private fun formatApiDate(date: String?): String {
        if (date.isNullOrBlank()) {
            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }

        return try {
            val year = date.substring(0, 4)
            val month = date.substring(5, 7)
            val day = date.substring(8, 10)
            "$day/$month/$year"
        } catch (e: Exception) {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
    }

    private fun sampleRecords(district: String): List<APMCCustomRecords> {
        val state = selectedState.takeIf { it != "None" } ?: "Maharashtra"
        return listOf(
            APMCCustomRecords(
                state,
                district,
                "$district Main Market",
                mutableListOf("Wheat", "Onion", "Tomato", "Cotton"),
                mutableListOf("2150", "1200", "900", "6200"),
                mutableListOf("2450", "1800", "1400", "7100")
            ),
            APMCCustomRecords(
                state,
                district,
                "$district Farmer Yard",
                mutableListOf("Maize", "Soyabean", "Groundnut"),
                mutableListOf("1850", "4100", "5200"),
                mutableListOf("2200", "4800", "5900")
            )
        )
    }

    private fun buildStateDistricts(): LinkedHashMap<String, Array<String>> {
        return linkedMapOf(
            "None" to arrayOf("None"),
            "Andhra Pradesh" to arrayOf(
                "None", "Anantapur", "Chittoor", "East Godavari", "Guntur", "Krishna",
                "Kurnool", "Prakasam", "Visakhapatnam", "West Godavari", "Kadapa"
            ),
            "Gujarat" to arrayOf(
                "None", "Ahmedabad", "Amreli", "Anand", "Banaskantha", "Bharuch",
                "Bhavnagar", "Gandhinagar", "Jamnagar", "Junagadh", "Kachchh",
                "Mehsana", "Rajkot", "Surat", "Vadodara", "Valsad"
            ),
            "Kerala" to arrayOf(
                "None", "Alappuzha", "Ernakulam", "Idukki", "Kannur", "Kollam",
                "Kottayam", "Kozhikode", "Malappuram", "Palakkad", "Thrissur"
            ),
            "Maharashtra" to arrayOf(
                "None", "Ahmednagar", "Akola", "Amravati", "Aurangabad", "Jalgaon",
                "Kolhapur", "Mumbai", "Nagpur", "Nashik", "Pune", "Sangli", "Satara",
                "Solapur", "Thane"
            ),
            "Rajasthan" to arrayOf(
                "None", "Ajmer", "Alwar", "Bharatpur", "Bikaner", "Jaipur",
                "Jodhpur", "Kota", "Sikar", "Udaipur"
            ),
            "Uttar Pradesh" to arrayOf(
                "None", "Agra", "Aligarh", "Bareilly", "Ghaziabad", "Gorakhpur",
                "Kanpur", "Lucknow", "Meerut", "Varanasi"
            ),
            "West Bengal" to arrayOf(
                "None", "Bankura", "Birbhum", "Darjeeling", "Hooghly", "Howrah",
                "Kolkata", "Malda", "Murshidabad", "Nadia"
            )
        )
    }
}
