#!/bin/bash
# Creates new worktrees for all IntelliJ IDEA repositories pointing to a new release branch.
# Usage: ./cloneToWt.sh <target directory> <branch>
# Restrictions:
#   None of the given Git repositories should have the branch with name <branch>.
#   There should be a remote branch origin/<branch> from which a new local branch will be created by this script.
#   You must use Git 2.5.0 or later.

set -e # Any command which returns non-zero exit code will cause this shell script to exit immediately

if [[ -z "$1" || -z "$2" ]] ; then
  echo "
  Usage:   ./cloneToWt.sh <target directory> <branch>
  Example: ./cloneToWt.sh ~/intellij-go-143 143"
  exit 1
fi

NEW_REPO="$1"
BRANCH="$2"

if [[ "$BRANCH" == origin/* ]]; then
  BRANCH="${BRANCH/origin\//}"
fi

# Absolute path to directory containing existing IntelliJ IDEA repo (and this script as well)
OLD_REPO="$(cd "`dirname "$0"`"; pwd)"
ROOTS=("/")

if [ -d "$NEW_REPO" ]; then
  echo "Directory '$NEW_REPO' already exists"
  exit 2
fi

for ROOT in ${ROOTS[@]}; do
  if [[ ! -z `git --git-dir="${OLD_REPO}${ROOT}/.git" --work-tree="${OLD_REPO}${ROOT}" branch --list $BRANCH` ]]; then
    echo "Branch '$BRANCH' already exists in $ROOT"
    exit 3
  fi
done

for ROOT in ${ROOTS[@]}; do
  git --git-dir="${OLD_REPO}${ROOT}/.git" --work-tree="${OLD_REPO}${ROOT}" worktree add -b $BRANCH "${NEW_REPO}${ROOT}" origin/${BRANCH}
done

cp -a "$OLD_REPO/.idea/workspace.xml" "$NEW_REPO/.idea/"
