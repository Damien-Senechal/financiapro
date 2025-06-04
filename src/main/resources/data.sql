-- Insertion des utilisateurs
INSERT INTO users (prenom, nom, email, api_key) VALUES
('Jean', 'Dupont', 'jean.dupont@example.com', '11111111-1111-1111-1111-111111111111'),
('Marie', 'Martin', 'marie.martin@example.com', '22222222-2222-2222-2222-222222222222'),
('Pierre', 'Bernard', 'pierre.bernard@example.com', '33333333-3333-3333-3333-333333333333'),
('Sophie', 'Dubois', 'sophie.dubois@example.com', '44444444-4444-4444-4444-444444444444');

-- Insertion des éléments de budget pour Jean
INSERT INTO budget_items (type, montant, description, date, user_id) VALUES
('INCOME', 2500.00, 'Salaire mensuel', '2025-06-01', 1),
('EXPENSE', 800.00, 'Loyer', '2025-06-05', 1),
('EXPENSE', 200.00, 'Courses alimentaires', '2025-06-10', 1),
('EXPENSE', 50.00, 'Transport', '2025-06-15', 1),
('INCOME', 150.00, 'Freelance', '2025-06-20', 1);

-- Insertion des éléments de budget pour Marie
INSERT INTO budget_items (type, montant, description, date, user_id) VALUES
('INCOME', 3000.00, 'Salaire mensuel', '2025-06-01', 2),
('EXPENSE', 1000.00, 'Loyer', '2025-06-05', 2),
('EXPENSE', 250.00, 'Courses alimentaires', '2025-06-10', 2),
('EXPENSE', 100.00, 'Loisirs', '2025-06-15', 2);

-- Insertion des éléments de budget pour Pierre
INSERT INTO budget_items (type, montant, description, date, user_id) VALUES
('INCOME', 2200.00, 'Salaire mensuel', '2025-06-01', 3),
('EXPENSE', 700.00, 'Loyer', '2025-06-03', 3),
('EXPENSE', 150.00, 'Courses', '2025-06-08', 3);

-- Insertion des demandes de prêt
INSERT INTO loan_requests (borrower_id, lender_id, montant, interet, duree, statut, date_creation, date_acceptation) VALUES
(1, 2, 500.00, 5.00, 6, 'ACCEPTED', '2025-05-15 10:00:00', '2025-05-16 14:30:00'),
(3, 2, 300.00, 3.00, 3, 'PENDING', '2025-06-01 09:00:00', NULL),
(4, 1, 1000.00, 7.00, 12, 'REFUSED', '2025-05-20 15:00:00', NULL);

-- Insertion des remboursements
INSERT INTO repayments (loan_request_id, montant, date, commentaire) VALUES
(1, 100.00, '2025-06-01', 'Premier remboursement'),
(1, 50.00, '2025-06-15', 'Remboursement partiel');