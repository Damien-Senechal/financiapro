-- Insertion des utilisateurs
INSERT INTO users (id, prenom, nom, email, api_key) VALUES
(1, 'Jean', 'Dupont', 'jean.dupont@example.com', '11111111-1111-1111-1111-111111111111'),
(2, 'Marie', 'Martin', 'marie.martin@example.com', '22222222-2222-2222-2222-222222222222'),
(3, 'Pierre', 'Bernard', 'pierre.bernard@example.com', '33333333-3333-3333-3333-333333333333'),
(4, 'Sophie', 'Dubois', 'sophie.dubois@example.com', '44444444-4444-4444-4444-444444444444');

-- Insertion des éléments de budget pour Jean (user 1)
INSERT INTO budget_items (id, type, montant, description, date, user_id) VALUES
(1, 'INCOME', 2500.00, 'Salaire mensuel', '2025-06-01', 1),
(2, 'EXPENSE', 800.00, 'Loyer', '2025-06-05', 1),
(3, 'EXPENSE', 200.00, 'Courses alimentaires', '2025-06-10', 1),
(4, 'EXPENSE', 50.00, 'Transport', '2025-06-15', 1),
(5, 'INCOME', 150.00, 'Freelance', '2025-06-20', 1);

-- Insertion des éléments de budget pour Marie (user 2)
INSERT INTO budget_items (id, type, montant, description, date, user_id) VALUES
(6, 'INCOME', 3000.00, 'Salaire mensuel', '2025-06-01', 2),
(7, 'EXPENSE', 1000.00, 'Loyer', '2025-06-05', 2),
(8, 'EXPENSE', 250.00, 'Courses alimentaires', '2025-06-10', 2),
(9, 'EXPENSE', 100.00, 'Loisirs', '2025-06-15', 2);

-- Insertion des éléments de budget pour Pierre (user 3)
INSERT INTO budget_items (id, type, montant, description, date, user_id) VALUES
(10, 'INCOME', 2200.00, 'Salaire mensuel', '2025-06-01', 3),
(11, 'EXPENSE', 700.00, 'Loyer', '2025-06-03', 3),
(12, 'EXPENSE', 150.00, 'Courses', '2025-06-08', 3);

-- Insertion des demandes de prêt
-- Jean demande un prêt à Marie (accepté)
INSERT INTO loan_requests (id, borrower_id, lender_id, montant, interet, duree, statut, date_creation, date_acceptation) VALUES
(1, 1, 2, 500.00, 5.00, 6, 'ACCEPTED', '2025-05-15 10:00:00', '2025-05-16 14:30:00');

-- Pierre demande un prêt à Marie (en attente)
INSERT INTO loan_requests (id, borrower_id, lender_id, montant, interet, duree, statut, date_creation, date_acceptation) VALUES
(2, 3, 2, 300.00, 3.00, 3, 'PENDING', '2025-06-01 09:00:00', NULL);

-- Sophie demande un prêt à Jean (refusé)
INSERT INTO loan_requests (id, borrower_id, lender_id, montant, interet, duree, statut, date_creation, date_acceptation) VALUES
(3, 4, 1, 1000.00, 7.00, 12, 'REFUSED', '2025-05-20 15:00:00', NULL);

-- Insertion des remboursements pour le prêt accepté
INSERT INTO repayments (id, loan_request_id, montant, date, commentaire) VALUES
(1, 1, 100.00, '2025-06-01', 'Premier remboursement'),
(2, 1, 50.00, '2025-06-15', 'Remboursement partiel');
