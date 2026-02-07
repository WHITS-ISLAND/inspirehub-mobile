# code-reviewer Memory

## Repeated Patterns

- Auth flow review: Check actual call paths (iOS GIDSignIn SDK vs Android mock) to identify dead code paths
- Always grep for `println` in shared/ to find sensitive log output -- common pattern in this codebase
- Trace PKCE flow end-to-end: verifier generation -> save -> retrieve -> send with token exchange

## Pain Points

- Two parallel auth strategies exist (PKCE auth-code flow + Google SDK ID token verify) causing confusion about which is active
- Android LoginScreen lacks real Google Sign-In integration (still on mockLogin)
- `println` used instead of a proper logging framework -- no build-type gating for log output

## Lessons Learned

- UserStore uses `kotlinx.coroutines.flow.MutableStateFlow` (not KMP-ObservableViewModel's) which is correct since it is not a ViewModel; the VM bridges via collect+transfer pattern
- `@ObservedViewModel` vs `@StateViewModel`: use @ObservedViewModel when VM is injected from parent, @StateViewModel when VM is created/owned by the View
- PKCE: must verify that `code_verifier` is sent during token exchange, not just saved -- incomplete PKCE is worse than no PKCE (false sense of security)
- Check API spec for endpoint existence (e.g., `/auth/logout` not listed in API docs)
