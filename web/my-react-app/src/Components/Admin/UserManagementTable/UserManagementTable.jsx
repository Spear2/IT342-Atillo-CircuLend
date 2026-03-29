import React from 'react';

const UserManagementTable = () => {
  return (
    <section className="admin-card">
      <div className="card-header">
        <span className="icon">👥</span> User Management
      </div>
      <table className="admin-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td></td>
            <td></td>
            <td></td>
            <td><button className="btn-small"></button></td>
          </tr>
        </tbody>
      </table>
    </section>
  );
};

export default UserManagementTable;