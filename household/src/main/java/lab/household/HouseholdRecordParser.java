package lab.household;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Shan Krishnan
 *
 */
public class HouseholdRecordParser {

	// HashMap to hold the parsed household record.
	// key is the field and value is parsed value.
	private HashMap<String, String> householdMap;

	/**************************************************************************
	 * Constructor householdRecordParser.
	 *************************************************************************/
	public HouseholdRecordParser() {
		householdMap = new HashMap<String, String>();
	}

	/**************************************************************************
	 * Parser method for household record.
	 *************************************************************************/
	public HashMap<String, String> parser(String hhRecord) {

		// get the DataDictionary parser object and the DataDictionary hashMap
		DataDictionaryParser ddParser = DataDictionaryParser.getInstance();
		HashMap<String, ArrayList<Integer>> ddHeader = ddParser.getDDHeader();

		// Loop through ddHeader to get the field name,size and location
		// and extract the data from the hhRecord. Push the key,value to
		// householdMap.
		for (String key : ddHeader.keySet()) {

			int beginIndex = ddHeader.get(key).get(0) - 1;
			int endIndex = beginIndex + ddHeader.get(key).get(1);
			String hhRecordValue = hhRecord.substring(beginIndex, endIndex);
			householdMap.put(key, hhRecordValue);
		}

		return householdMap;

	}

}
