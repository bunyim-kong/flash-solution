# Admin area status

## Included and connected
- Admin dashboard layout and navigation
- Product list, search, status filter, backend add/edit and backend delete
- User management backend list, create, role change and delete
- Brand and category selectors loaded from backend tables
- Admin login screen design is not connected to a real authentication service

## Not yet secure / not yet implemented
The frontend currently does not authenticate administrators or enforce permissions. Catalogue and user CRUD are connected, but the backend endpoints are public until Spring Security is implemented. The cart remains in localStorage because there is no cart table.

## Student implementation task (Spring Boot)
Students should implement:
1. Authentication endpoint and secure password verification
2. Token or session handling
3. ADMIN and CUSTOMER roles
4. Backend authorization for all admin-only endpoints
5. Frontend login/logout integration
6. Route handling for unauthenticated and unauthorized users

Do not treat a hidden frontend route or a localStorage role flag as security. The backend must enforce access control.
