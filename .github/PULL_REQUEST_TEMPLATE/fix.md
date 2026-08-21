Fixes #ISSUE

### Instructions to reviewer on how to test:
1. Replicate #ISSUE was observed on default branch:
  a. Do setup
  n. Perform problematic action
  z. Observe incorrect behaviour
2. Confirm #ISSUE is not observed with this branch:
  a. Follow setup from above [or modified setup]
  n. Perform problematic action
  z. Observe no incorrect behaviour
3. Ensure tests cover problematic behaviour

### Checks for reviewer
- [ ] Would the PR title make sense to a user on a set of release notes and begin with `fix:`?
- [ ] If the change include breaking changes (to database or api) does the title begin `fix!:`?
- [ ] Are the commits reasonably atomic to re-create the change?
- [ ] Does the title cover all changes made in this PR? (Contain all changes AND only the changes)
