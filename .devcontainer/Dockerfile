FROM mcr.microsoft.com/devcontainers/base:resolute

# Install helm for the dev container. This is the recommended
# approach per the docs: https://helm.sh/docs/intro/install
RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-4; \
    chmod 700 get_helm.sh; \
    ./get_helm.sh; \
    rm get_helm.sh
