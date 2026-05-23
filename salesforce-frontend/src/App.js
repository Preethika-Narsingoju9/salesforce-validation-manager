import React, { useState } from "react";
import axios from "axios";
import "./App.css";

function App() {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);

  const API_BASE =
    "https://salesforce-validation-manager-snah.onrender.com";

  // LOGIN TO SALESFORCE

  const loginToSalesforce = () => {
  window.location.href = `${API_BASE}/api/login`;
};


  // const loginToSalesforce = () => {
  //   window.location.href = `${API_BASE}/api/login`;
  // };

//   const loginToSalesforce = () => {
//   const clientId = "3MVG97L7PWbPq6UwCL.6YvIjV90HG23keKInIpqpKBwC0bwHPdUdg8OJmqYkTHDhnnS4OUmE5QdfydRcRoTaQ";

//   const redirectUri =
//     "https://salesforce-validation-manager-snah.onrender.com/api/callback";

//   const loginUrl =
//     `https://login.salesforce.com/services/oauth2/authorize` +
//     `?response_type=code` +
//     `&client_id=${clientId}` +
//     `&redirect_uri=${encodeURIComponent(redirectUri)}`;

//   window.location.href = loginUrl;
// };

  // GET VALIDATION RULES
  const getValidationRules = async () => {
    try {
      setLoading(true);

      const response = await axios.get(`${API_BASE}/api/validation-rules`);

      setRules(response.data?.records ?? []);
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
        `${API_BASE}/toggle-rule?id=${id}&active=${!currentStatus}`
      );

      alert("Validation Rule Updated");

      // refresh list
      getValidationRules();
    } catch (error) {
      console.error(error);
      alert("Error updating validation rule");
    }
  };

  // DEPLOY BUTTON
  const deployChanges = () => {
    alert(
      "Changes are already deployed directly to Salesforce via Tooling API."
    );
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
                <td>{rule.Name}</td>

                <td>{rule.Active ? "Active" : "Inactive"}</td>

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