import React, { useState } from "react";
import axios from "axios";
import "./App.css";

function App() {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);

  // LOGIN TO SALESFORCE
  const loginToSalesforce = () => {
    window.location.href = "https://salesforce-validation-manager-snah.onrender.com/login";
  };

  // GET VALIDATION RULES
  const getValidationRules = async () => {
    try {
      setLoading(true);

      const response = await axios.get(
        "https://salesforce-validation-manager-snah.onrender.com/validation-rules"
      );

      setRules(response.data.records || []);

    } catch (error) {
      console.error(error);
      alert("Error fetching validation rules");
    } finally {
      setLoading(false);
    }
  };

  // TOGGLE RULE
  const toggleRule = async (id, currentStatus) => {
    try {
      await axios.get(
        `https://salesforce-validation-manager-snah.onrender.com/toggle-rule?id=${id}&active=${!currentStatus}`
      );

      alert("Validation Rule Updated");

      // refresh list after update
      getValidationRules();

    } catch (error) {
      console.error(error);
      alert("Error updating validation rule");
    }
  };

  // DEPLOY BUTTON (REQUIRED BY ASSIGNMENT)
  const deployChanges = () => {
    alert("Changes are already deployed directly to Salesforce via Tooling API.");
  };

  return (
    <div className="container">

      <h1>Salesforce Validation Rule Manager</h1>

      

      
      <div className="button-container">

  <button onClick={loginToSalesforce}>
    Login to Salesforce
  </button>

  <button onClick={getValidationRules}>
    Get Validation Rules
  </button>

  <button className="deploy-btn" onClick={deployChanges}>
    Deploy Changes
  </button>

</div>

      {/* LOADING STATE */}
      {loading && <p>Loading validation rules...</p>}

      {/* TABLE */}
      {rules.length > 0 && (
        <table>

          <thead>
            <tr>
              <th>Rule Name</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {rules.map((rule) => (
              <tr key={rule.Id}>

                <td>{rule.ValidationName}</td>

                <td>
                  {rule.Active ? "Active" : "Inactive"}
                </td>

                <td>
                  <button
                    onClick={() =>
                      toggleRule(rule.Id, rule.Active)
                    }
                  >
                    {rule.Active ? "Deactivate" : "Activate"}
                  </button>
                </td>

              </tr>
            ))}
          </tbody>

        </table>
      )}

    </div>
  );
}

export default App;