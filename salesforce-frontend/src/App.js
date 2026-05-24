import React, { useState } from "react";
import axios from "axios";
import "./App.css";

function App() {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetched, setFetched] = useState(false);

  const API_BASE =
    "https://salesforce-validation-manager-snah.onrender.com";

  // ---------------- AXIOS INSTANCE ----------------
  const axiosInstance = axios.create({
    baseURL: API_BASE,
    withCredentials: true,
  });

  // ---------------- LOGIN ----------------
  const loginToSalesforce = () => {
    window.location.href = `${API_BASE}/api/login`;
  };

  // ---------------- GET VALIDATION RULES ----------------
  const getValidationRules = async () => {
    try {
      setLoading(true);

      const response = await axiosInstance.get(
        "/api/validation-rules"
      );

      setRules(response.data.records || []);
      setFetched(true);

    } catch (error) {
      console.error(error);
      alert("Failed fetching validation rules");
    } finally {
      setLoading(false);
    }
  };

  // ---------------- TOGGLE RULE ----------------
  const toggleRule = async (id, currentStatus) => {
    try {

      await axiosInstance.get(
        `/api/toggle-rule?id=${id}&active=${!currentStatus}`
      );

      // UI update instantly
      setRules((prevRules) =>
        prevRules.map((rule) =>
          rule.Id === id
            ? {
                ...rule,
                Active: !currentStatus,
              }
            : rule
        )
      );

      alert("Validation Rule Updated");

    } catch (error) {
      console.error(error);
      alert("Error updating validation rule");
    }
  };

  // ---------------- DEPLOY BUTTON ----------------
  const deployChanges = () => {
    alert(
      "Changes are deployed directly to Salesforce using Tooling API."
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
          Validation Rules
        </button>

        <button
          className="deploy-btn"
          onClick={deployChanges}
        >
          Deploy Changes
        </button>

      </div>

      {/* Loading Message */}
      {loading && (
        <p>Loading validation rules...</p>
      )}

      {/* Show only after fetch */}
      {fetched && !loading && rules.length === 0 && (
        <p>No validation rules found</p>
      )}

      {/* Rules Table */}
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

                <td>
                  {rule.ValidationName || rule.Name}
                </td>

                <td>
                  {rule.Active ? "Active" : "Inactive"}
                </td>

                <td>
                  <button
                    onClick={() =>
                      toggleRule(
                        rule.Id,
                        rule.Active
                      )
                    }
                  >
                    {rule.Active
                      ? "Deactivate"
                      : "Activate"}
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