Vagrant.configure('2') do |config|

  # Declaring VM Name 
  config.vm.define "webserver" do |config|
 
 # Define Ubuntu Version 
  config.vm.box = "ubuntu/xenial64"

# Define Provider
  config.vm.provider "virtualbox" do |vb|
    #   # Display the VirtualBox GUI when booting the machine (Debug)
    #   vb.gui = true
    #
    # Customize the amount of memory on the VM:
    vb.memory = "1024"

    #Fix Port Forwarding 

    config.vm.network "forwarded_port", guest: 8080, host: 8080,
    auto_correct: true

  end

  # Enable provisioning with Ansible.
  config.vm.provision "ansible" do |ansible|
    ansible.playbook = "ansible/jenkins.yml"
end

end
end
