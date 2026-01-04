import React, { useState, useEffect } from "react";
import "./ClientDashboard.css";

const ClientDashboard = () => {
  // Load balance from localStorage or use default 1000
  const [balance, setBalance] = useState(() => {
    const savedBalance = localStorage.getItem("dashboardBalance");
    return savedBalance ? parseFloat(savedBalance) : 1000;
  });
  
  const [status, setStatus] = useState("Verified");
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");
  const [targetAccount, setTargetAccount] = useState("");
  const [showTransferOptions, setShowTransferOptions] = useState(false);
  
  const targetAccounts = [
    { id: "789012", name: "Ahmed Hassan", type: "Checking" },
    { id: "345678", name: "Sara Ahmed", type: "Savings" },
    { id: "901234", name: "Omar Ali", type: "Checking" }
  ];
  
  // Format date to be more readable
  const formatDate = (date) => {
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };
  
  const [transactions, setTransactions] = useState(() => {
    const savedTransactions = localStorage.getItem("dashboardTransactions");
    if (savedTransactions) {
      return JSON.parse(savedTransactions);
    }
    return [
      { 
        id: 1, 
        date: formatDate(new Date(2024, 0, 15, 10, 30)), 
        type: "Initial Balance", 
        amount: 1000, 
        balance: 1000 
      }
    ];
  });
  
  const [showStatement, setShowStatement] = useState(false);

  // Save balance to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem("dashboardBalance", balance.toString());
  }, [balance]);

  // Save transactions to localStorage whenever they change
  useEffect(() => {
    localStorage.setItem("dashboardTransactions", JSON.stringify(transactions));
  }, [transactions]);

  // Load status from localStorage
  useEffect(() => {
    const savedStatus = localStorage.getItem("dashboardStatus");
    if (savedStatus) {
      setStatus(savedStatus);
    }
  }, []);

  // Save status to localStorage
  useEffect(() => {
    localStorage.setItem("dashboardStatus", status);
  }, [status]);

  const getStatusClass = () => {
    switch(status.toLowerCase()) {
      case "verified": return "status-verified";
      case "suspended": return "status-suspended";
      case "closed": return "status-closed";
      default: return "status-verified";
    }
  };

  const getStatusIcon = () => {
    switch(status) {
      case "Verified": return "✅";
      case "Suspended": return "⛔";
      case "Closed": return "🔒";
      default: return "✅";
    }
  };

  const getDashboardStatusClass = () => {
    switch(status.toLowerCase()) {
      case "suspended": return "suspended";
      case "closed": return "closed";
      default: return "";
    }
  };


  
  const isDepositAllowed = status === "Verified" || status === "Suspended"; // FIXED
  const isWithdrawalAllowed = status === "Verified"; // Only for Verified
  const isTransferAllowed = status === "Verified"; // Only for Verified
  const isViewStatementAllowed = true; // Always allowed 

  const addTransaction = (type, transactionAmount, newBalance) => {
    const newTransaction = {
      id: transactions.length + 1,
      date: formatDate(new Date()),
      type: type,
      amount: transactionAmount,
      balance: newBalance
    };
    const updatedTransactions = [newTransaction, ...transactions];
    setTransactions(updatedTransactions);
  };

  const handleDeposit = () => {
    if (!isDepositAllowed) {
      setMessage(`Error: Deposits are illegal for ${status} accounts`);
      return;
    }
    if (amount <= 0 || isNaN(amount)) {
      setMessage("Error: Invalid amount");
      return;
    }
    const newBalance = balance + Number(amount);
    setBalance(newBalance);
    addTransaction("Deposit", Number(amount), newBalance);
    setMessage(`Deposit of $${amount} successful`);
    setAmount("");
  };

  const handleWithdraw = () => {
    if (!isWithdrawalAllowed) {
      setMessage(`Error: Withdrawals are illegal for ${status} accounts`);
      return;
    }
    if (amount <= 0 || isNaN(amount)) {
      setMessage("Error: Invalid amount");
      return;
    }
    if (amount > balance) {
      setMessage("Error: Insufficient balance");
      return;
    }
    const newBalance = balance - Number(amount);
    setBalance(newBalance);
    addTransaction("Withdrawal", Number(amount), newBalance);
    setMessage(`Withdrawal of $${amount} successful`);
    setAmount("");
  };

  const handleTransferClick = () => {
    if (!isTransferAllowed) {
      setMessage(`Error: Transfers are illegal for ${status} accounts`);
      return;
    }
    setShowTransferOptions(!showTransferOptions);
    if (!showTransferOptions) {
      setMessage("Please select a target account for the transfer");
    } else {
      setMessage("");
      setTargetAccount("");
    }
  };

  const handleConfirmTransfer = () => {
    if (!targetAccount) {
      setMessage("Error: Please select a target account");
      return;
    }
    if (amount <= 0 || isNaN(amount)) {
      setMessage("Error: Invalid amount");
      return;
    }
    if (amount > balance) {
      setMessage("Error: Insufficient balance");
      return;
    }
    const selectedAccount = targetAccounts.find(acc => acc.id === targetAccount);
    const newBalance = balance - Number(amount);
    setBalance(newBalance);
    addTransaction(`Transfer to ${selectedAccount.name}`, Number(amount), newBalance);
    setMessage(`Transfer of $${amount} to ${selectedAccount.name} (${selectedAccount.id}) completed`);
    setAmount("");
    setTargetAccount("");
    setShowTransferOptions(false);
  };

  const handleViewStatement = () => {
    setShowStatement(!showStatement);
    setMessage(showStatement ? "" : "Showing transaction history");
  };

  const handleStatusChange = (newStatus) => {
    setStatus(newStatus);
    setMessage(`Account status changed to ${newStatus}`);
    
    // Clear the amount input for Closed accounts only (Suspended can still deposit)
    if (newStatus === "Closed") {
      setAmount("");
    }
  };

  return (
    <div className={`dashboard ${getDashboardStatusClass()}`}>
      <h2>Client Dashboard</h2>

      {/* My Account Section */}
      <div className="status-section">
        <h3>My Account</h3>
      </div>

      {/* Account Details */}
      <div className="info">
        <p><strong>Client Name:</strong> Mariam Riyad</p>
        <p><strong>Account Number:</strong> 123456</p>
        <p><strong>Account Type:</strong> Savings Account</p>
        <p><strong>Balance:</strong> ${balance.toFixed(2)}</p>
        <p><strong>Status:</strong> 
          <span className={`status-badge ${getStatusClass()}`}>
            {getStatusIcon()} {status}
          </span>
        </p>
        
        {/* Status-specific warnings - UPDATED */}
        {status !== "Verified" && (
          <div className={`status-warning ${status.toLowerCase()}`}>
            {status === "Suspended"
              ? "⛔ Account suspended. Withdraw and Transfer are illegal actions. Deposit allowed." // UPDATED
              : "🔒 Account closed. Deposit and Withdraw are illegal actions. View only."}
          </div>
        )}
      </div>

      {/* States of Account Section */}
      <div className="status-section">
        <h3>Account Status Options</h3>
      </div>

      {/* Status Grid */}
      <div className="status-grid">
        <div 
          className={`status-card verified ${status === "Verified" ? "active" : ""}`}
          onClick={() => handleStatusChange("Verified")}
        >
          <div className="status-icon-lg">✅</div>
          <h4>Verified</h4>
          <p>Deposit, Withdraw allowed</p>
        </div>
        
        <div 
          className={`status-card suspended ${status === "Suspended" ? "active" : ""}`}
          onClick={() => handleStatusChange("Suspended")}
        >
          <div className="status-icon-lg">⛔</div>
          <h4>Suspended</h4>
          <p>Deposit allowed, Withdraw & Transfer illegal</p> {/* UPDATED */}
        </div>

        <div 
          className={`status-card closed ${status === "Closed" ? "active" : ""}`}
          onClick={() => handleStatusChange("Closed")}
        >
          <div className="status-icon-lg">🔒</div>
          <h4>Closed</h4>
          <p>View only, Deposit & Withdraw illegal</p>
        </div>
      </div>

      {/* Status Legend */}
      <div className="status-legend">
        <div className="legend-item">
          <div className="legend-color legend-verified"></div>
          <span>Verified - Deposit, Withdraw</span>
        </div>
        <div className="legend-item">
          <div className="legend-color legend-suspended"></div>
          <span>Suspended - Deposit allowed</span> {/* UPDATED */}
        </div>
        <div className="legend-item">
          <div className="legend-color legend-closed"></div>
          <span>Closed - View Only</span>
        </div>
      </div>

      {/* Transaction Section */}
      <div className="transaction-section">
        {/* ADDED THIS SECTION */}
        <div className="transaction-instruction">
          Please enter the amount, then select an option below.
        </div>
        
       
        {/* Amount Input */}
        <input
          type="number"
          placeholder="Enter amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          min="0"
          step="0.01"
          disabled={status === "Closed"}
          className="amount-input"
        />
        
        {/* Target Account Selection for Transfers - Only visible when Transfer is clicked */}
        {showTransferOptions && (
          <div className="transfer-options">
            <select
              value={targetAccount}
              onChange={(e) => setTargetAccount(e.target.value)}
              className="target-account-select"
            >
              <option value="">Select target account for transfer</option>
              {targetAccounts.map((acc) => (
                <option key={acc.id} value={acc.id}>
                  {acc.name} - {acc.id} ({acc.type})
                </option>
              ))}
            </select>
            <button 
              onClick={handleConfirmTransfer}
              className="confirm-transfer-btn"
              disabled={!targetAccount}
            >
              Confirm Transfer
            </button>
          </div>
        )}
        
        {/* Action Buttons */}
        <div className="buttons">
          <button 
            onClick={handleDeposit} 
            disabled={!isDepositAllowed}
            title={!isDepositAllowed ? `Deposits illegal for ${status} accounts` : ""}
          >
            Deposit
          </button>

          <button 
            onClick={handleWithdraw} 
            disabled={!isWithdrawalAllowed}
            title={!isWithdrawalAllowed ? `Withdrawals illegal for ${status} accounts` : ""}
          >
            Withdraw
          </button>

          <button 
            onClick={handleTransferClick} 
            disabled={!isTransferAllowed}
            title={!isTransferAllowed ? `Transfers illegal for ${status} accounts` : ""}
            className={showTransferOptions ? "active" : ""}
          >
            {showTransferOptions ? "Cancel Transfer" : "Transfer"}
          </button>

          <button 
            onClick={handleViewStatement}
            disabled={!isViewStatementAllowed}
          >
            {showStatement ? "Hide Statement" : "View Statement"}
          </button>
        </div>
        

        {/* Restriction notes - UPDATED */}
        {status !== "Verified" && (
          <div className="restriction-note">
            {status === "Suspended" 
              ? "Account suspended - Withdraw and Transfer are illegal (Deposit allowed)" // UPDATED
              : "Account closed - Deposit and Withdraw are illegal"}
          </div>
        )}
      </div>

      {/* Message Display */}
      <p className="message">{message}</p>

      {/* Transaction Statement */}
      {showStatement && (
        <div className="statement">
          <h3>Transaction History</h3>
          <div className="transaction-list">
            <div className="transaction-header">
              <span className="date-col">Date & Time</span>
              <span className="type-col">Type</span>
              <span className="amount-col">Amount</span>
              <span className="balance-col">Balance</span>
            </div>
            {transactions.map((tx) => (
              <div key={tx.id} className={`transaction-row ${tx.type.toLowerCase().replace(' ', '-')}`}>
                <span className="date-col">{tx.date}</span>
                <span className={`type-col ${tx.type === 'Deposit' ? 'deposit' : tx.type === 'Initial Balance' ? 'initial' : 'withdrawal'}`}>
                  {tx.type}
                </span>
                <span className={`amount-col ${tx.type === 'Deposit' || tx.type === 'Initial Balance' ? 'positive' : 'negative'}`}>
                  {tx.type === 'Deposit' || tx.type === 'Initial Balance' ? '+' : '-'}${tx.amount.toFixed(2)}
                </span>
                <span className="balance-col">${tx.balance.toFixed(2)}</span>
              </div>
            ))}
          </div>
          <div className="statement-summary">
            <p>Total Transactions: <strong>{transactions.length}</strong></p>
            <p>Current Balance: <strong>${balance.toFixed(2)}</strong></p>
            <p>Account Status: <strong className={getStatusClass()} style={{color: 'inherit', background: 'none', boxShadow: 'none', padding: '0'}}>{status}</strong></p>
          </div>
        </div>
      )}
    </div>
  );
};

export default ClientDashboard;