pragma solidity ^ 0.4 .16;
contract owned {
    address public owner;

    function owned() public {
        owner = msg.sender;
    }

    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }

    function transferOwnership(address newOwner) onlyOwner public {
        owner = newOwner;
    }
}

contract token {
    /* Public variables of the token */
    string public standard = 'Token 0.1';
    string public name;
    string public symbol;

    function token(
        string tokenName,
        string tokenSymbol
    ) public {
        name = tokenName; // Set the name for display purposes
        symbol = tokenSymbol; // Set the symbol for display purposes
    }


}

contract XbrlFr is owned, token {

    mapping(address => bool) public frozenAccount;

    event FrozenAccount(address target, bool frozen);

    /*
     * Initializes contract with initial supply tokens to the creator of the
     * contract
     */
    function XbrlFr(
        string tokenName,
        string tokenSymbol
    ) token(tokenName, tokenSymbol) public {

    }

    struct Report {
        string reportId;
        string date;
        string validated;
        string shash;
    }
    struct Company {
        address companyAddress;
        string companyName;
        Report[] reports;
    }
    Company[] public companies;

	function ownCompany() public view returns(uint rs, uint companyIndex) {
		//return 1: msg.sender own company
		//return 0: no
		
		uint index;
        for (index = 0; index < companies.length; index++) {
        	if (msg.sender == companies[index].companyAddress) {
                return (1, index);
            }
        }
		
		return (0,0);
	}
    function ownReport(string reportId) public view returns(uint rs, uint companyIndex, uint reportIndex) {
        // return 0 : report id does not exits
        // return 1 : report id exits and belong to you
        uint checkOwnCompany;
        (checkOwnCompany, companyIndex) = ownCompany(); 
        
        require(checkOwnCompany == 1);
        
        uint index;
        
        for (index = 0; index < companies[companyIndex].reports.length; index++) {
            if (strCompare(companies[companyIndex].reports[index].reportId, reportId) == 0) {
                    return (1, companyIndex, index);
            }
        }
        return (0,companyIndex,0);
    }
    
    function registerNewCompany(string companyName) public {
    	uint index;
    	uint cpIndex = companies.length + 1;
    	for (index = 0; index < companies.length; index++) {
    		require(msg.sender != companies[index].companyAddress);
    	}
    	cpIndex = companies.length;
    	companies.length += 1;
    	companies[cpIndex].companyAddress = msg.sender;
    	companies[cpIndex].companyName = companyName;
    }
    
    function getCompany(address companyAddress) 
    	public constant returns (address cpAddress, string companyName, uint reportSize, uint companyIndex) {
        uint index;
    	for (index = 0; index < companies.length; index++) {
    		if (companyAddress == companies[index].companyAddress) {
    			return (companyAddress, companies[index].companyName, companies[index].reports.length, index);
    		}
    	}
    }
    
    function addReport(string reportId, string date, string validated, string shash) public {
        uint checkOwnReport;
        uint companyIndex;
        uint reportIndex;
        (checkOwnReport,companyIndex, reportIndex )= ownReport(reportId);
        require(checkOwnReport == 0);

        uint rpIndex = companies[companyIndex].reports.length;
        companies[companyIndex].reports.length += 1;
        companies[companyIndex].reports[rpIndex].reportId = reportId;
        companies[companyIndex].reports[rpIndex].date = date;
        companies[companyIndex].reports[rpIndex].validated = validated;
        companies[companyIndex].reports[rpIndex].shash = shash;
    }
    
    function updateValidateReport(string reportId, string date, string validated, string shash) public {
        uint checkOwnReport;
        uint companyIndex;
        uint reportIndex;
        (checkOwnReport,companyIndex, reportIndex )= ownReport(reportId);
        require(checkOwnReport == 1);
        companies[companyIndex].reports[reportIndex].date = date;
        companies[companyIndex].reports[reportIndex].validated = validated;
        companies[companyIndex].reports[reportIndex].shash = shash;
    }
    
    /*
    function getReport(address companyAddress, string reportId) public constant returns (string rpId, uint reportIndex, string date, string validated, uint factSize, uint arcSize) {
        uint index;
        uint indexRp;
    	for (index = 0; index < companies.length; index++) {
    		if (companyAddress == companies[index].companyAddress) {
    			for (indexRp = 0; indexRp < companies[index].reports.length; indexRp++) {
    	    		if (strCompare(reportId,companies[index].reports[indexRp].reportId) == 0) {
    	    			return (reportId, indexRp,
    	    					companies[index].reports[indexRp].date, companies[index].reports[indexRp].validated,
    	    					companies[index].reports[indexRp].facts.length, companies[index].reports[indexRp].arcs.length);
    	    		}
    	    	}
    		}
    	}
    	return ("",0,"","",0,0);
    }
    */

    
   
    function getReportByIndex(uint companyIndex, uint reportIndex) 
    	public constant returns (string rpId, uint rpIndex, string date, string validated, string shash) {

		return (companies[companyIndex].reports[reportIndex].reportId, 
				reportIndex,
				companies[companyIndex].reports[reportIndex].date, 
				companies[companyIndex].reports[reportIndex].validated,
				companies[companyIndex].reports[reportIndex].shash);
    }
    
    

    
//
//    function registerNewCompany(string companyName) public {
//        uint index;
//        uint cpIndex = companies.length + 1;
//        for (index = 0; index < companies.length; index++) {
//            require(msg.sender != companies[index].companyAddress);
//        }
//        cpIndex = companies.length;
//        companies.length += 1;
//        companies[cpIndex].companyAddress = msg.sender;
//        companies[cpIndex].companyName = companyName;
//    }
//
//    function getFact(string reportId, string concept) public constant returns(string fact) {
//        uint reportIndex = ownReport(reportId);
//        require(reportIndex == 2);
//
//        uint index;
//        for (index = 0; index < facts.length; index++) {
//            if (strCompare(facts[index].reportId, reportId) == 0) {
//                if (strCompare(facts[index].concept, concept) == 0) {
//                    // concept = facts[index].concept;
//                    // context = facts[index].context;
//                    // value = facts[index].value;
//                    // factgroup = facts[index].factgroup;
//                    return strConcat(facts[index].concept, facts[index].context, facts[index].value, facts[index].factgroup, "");
//                }
//            }
//        }
//
//        return "no-value";
//    }

    // function getFactObj(string reportId, string concept) public constant returns(string concept, string context, string value, string factgroup) {
    //     uint reportIndex = ownReport(reportId);
    //     require(reportIndex == 2);

    //     uint index;
    //     for (index = 0; index < facts.length; index++) {
    //         if (strCompare(facts[index].reportId, reportId) == 0) {
    //             if (strCompare(facts[index].concept, concept) == 0) {
    //                 concept = facts[index].concept;
    //                 context = facts[index].context;
    //                 value = facts[index].value;
    //                 factgroup = facts[index].factgroup;
    //                 return (concept, context, value, factgroup);
    //             }
    //         }
    //     }

    //     return "no-value";
    // }
    
    //function getCompany(address companyAddress) public constant returns(string address, string companyName, uint reportSize) {
    	//for (index = 0; index < companies.length; index++) {
        //    if (companyAddress == companies[index].companyAddress) {
        //    	return (companyAddress, companies[index].companyName, companies[index])
        //    }
        //}
    //}

    // API:
    function strCompare(string _a, string _b) internal pure returns(int) {
        bytes memory a = bytes(_a);
        bytes memory b = bytes(_b);
        uint minLength = a.length;
        if (b.length < minLength) minLength = b.length;
        for (uint i = 0; i < minLength; i++)
            if (a[i] < b[i])
                return -1;
            else if (a[i] > b[i])
            return 1;
        if (a.length < b.length)
            return -1;
        else if (a.length > b.length)
            return 1;
        else
            return 0;
    }

    function strConcat(string _a, string _b, string _c, string _d, string _e) internal pure returns(string) {
        bytes memory _ba = bytes(_a);
        bytes memory _bb = bytes(_b);
        bytes memory _bc = bytes(_c);
        bytes memory _bd = bytes(_d);
        bytes memory _be = bytes(_e);
        string memory abcde = new string(_ba.length + _bb.length + _bc.length + _bd.length + _be.length);
        bytes memory babcde = bytes(abcde);
        uint k = 0;
        for (uint i = 0; i < _ba.length; i++) babcde[k++] = _ba[i];
        for (i = 0; i < _bb.length; i++) babcde[k++] = _bb[i];
        for (i = 0; i < _bc.length; i++) babcde[k++] = _bc[i];
        for (i = 0; i < _bd.length; i++) babcde[k++] = _bd[i];
        for (i = 0; i < _be.length; i++) babcde[k++] = _be[i];
        return string(babcde);
    }

}